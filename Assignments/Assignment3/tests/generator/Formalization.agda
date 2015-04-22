module Formalization where

open import Data.Bool
open import Data.Nat using (ℕ)
open import Data.Integer using (ℤ; +_; -[1+_])
open import Data.List
open import Data.List.Any hiding (map)
open import Data.Product hiding (map)
open import Data.Unit hiding (Unit)
open import Data.String hiding (_++_; _==_)
open import Relation.Binary.PropositionalEquality hiding ([_])
open import Function

data ExprType : Set where
  integer : ExprType
  boolean : ExprType

data DeclCtor : Set where
  expr    : DeclCtor
  array1D : DeclCtor
  array2D : DeclCtor

data VarType : Set where
  decl           : ExprType → DeclCtor → VarType
  procedure-type : List ExprType → VarType
  function-type  : ExprType → List ExprType → VarType

data MajorScope : Set where
  top-scope       : MajorScope
  procedure-scope : MajorScope
  function-scope  : ExprType → MajorScope

data MinorScope : Set where
  no-scope   : MinorScope
  loop-scope : MinorScope

Ref : VarType → List VarType → Set
Ref τ Γ = Any (_≡_ τ) Γ

z : ∀ {τ Γ} → Ref τ (τ ∷ Γ)
z = here refl

s : ∀ {τ υ Γ} → Ref τ Γ → Ref τ (υ ∷ Γ)
s = there

-- Form: Stmt Γ Γ′ +s -s r
-- Γ  - The list of variables in scope before the statement.
-- Γ′ - The list of variables in scope after the statement.
-- +s - The type of major scope required for the statement.
-- -s - The type of minor scope required for the statement.
-- r  - Whether or not the statement causes a function or procedure to exit.
data Stmt Γ : List VarType → MajorScope → MinorScope → Bool → Set
data Expr Γ +s : Bool → ExprType → Set

Program : Set
Program = Stmt [] [] top-scope no-scope false

data Output Γ +s : Bool → Set where
  out-string : String → Output Γ +s false
  out-expr   : ∀ {r} → Expr Γ +s r integer → Output Γ +s r
  out-skip   : Output Γ +s false

infixr 4 _∷ₒ_

data OutputList Γ +s : Bool → Set where
  []ₒ : OutputList Γ +s false
  _∷ₒ_ : ∀ {r₁ r₂} → Output Γ +s r₁ → OutputList Γ +s r₂ → OutputList Γ +s (r₁ ∧ r₂)

tuplefy : List VarType → MajorScope → Bool → List ExprType → Set
tuplefy Γ +s r [] = ⊤
tuplefy Γ +s r (x ∷ xs) = Expr Γ +s r x × tuplefy Γ +s r xs

infixl 0 _∙_

data Stmt Γ where
  -- Variable Declaration and Assignment
  def : ∀ {+s -s} τ (xs : List DeclCtor) → Stmt Γ (map (decl τ) xs ++ Γ) +s -s false
  var_:= : ∀ {+s -s r a} → Ref (decl a expr) Γ → Expr Γ +s r a → Stmt Γ Γ +s -s r
  var_[_]:= : ∀ {+s -s r₁ r₂ a} → Ref (decl a array1D) Γ → Expr Γ +s r₁ integer → Expr Γ +s r₂ a → Stmt Γ Γ +s -s (r₁ ∨ r₂)
  var_[_,_]:= : ∀ {+s -s r₁ r₂ r₃ a}
              → Ref (decl a array2D) Γ
              → Expr Γ +s r₁ integer
              → Expr Γ +s r₂ integer
              → Expr Γ +s r₃ a
              → Stmt Γ Γ +s -s (r₁ ∨ r₂ ∨ r₃)

  -- Conditionals
  if_then_end : ∀ {Γ′ +s -s r₁ r₂} → Expr Γ +s r₁ boolean → Stmt Γ Γ′ +s -s r₂ → Stmt Γ Γ′ +s -s r₁
  if_then_else_end : ∀ {Γ′ Γ″ +s -s r₁ r₂ r₃}
                   → Expr Γ +s r₁ boolean
                   → Stmt Γ Γ′ +s -s r₂
                   → Stmt Γ′ Γ″ +s -s r₃
                   → Stmt Γ Γ″ +s -s (r₁ ∧ r₂ ∧ r₃)

  -- Loops
  while_do_end : ∀ {Γ′ +s -s r₁ r₂} → Expr Γ +s r₁ boolean → Stmt Γ Γ′ +s -s r₂ → Stmt Γ Γ′ +s -s r₁
  loop_end : ∀ {Γ′ +s -s r} → Stmt Γ Γ′ +s -s r → Stmt Γ Γ′ +s -s r
  exit : ∀ {+s} → Stmt Γ Γ +s loop-scope false
  exit-when : ∀ {+s r} → Expr Γ +s r boolean → Stmt Γ Γ +s loop-scope r

  -- I/O
  put : ∀ {+s -s r} → OutputList Γ +s r → Stmt Γ Γ +s -s r
  get : ∀ {+s -s f} → List (Ref (decl integer f) Γ) → Stmt Γ Γ +s -s false

  -- Functions and Procedures
  call : ∀ {+s -s xs} → Ref (procedure-type xs) Γ → tuplefy Γ +s false xs → Stmt Γ Γ +s -s false
  function : ∀ {Γ′ +s -s} τ xs
           → Stmt (map (flip decl expr) xs ++ Γ) Γ′ (function-scope τ) no-scope true
           → Stmt Γ (function-type τ xs ∷ Γ) +s -s false
  procedure : ∀ {Γ′ +s -s r} xs → Stmt (map (flip decl expr) xs ++ Γ) Γ′ procedure-scope no-scope r → Stmt Γ (procedure-type xs ∷ Γ) +s -s false
  return-[_] : ∀ { -s r a} → Expr Γ (function-scope a) r a → Stmt Γ Γ (function-scope a) -s true
  return : ∀ { -s} → Stmt Γ Γ procedure-scope -s true

  -- Scoping and Sequencing
  begin_end : ∀ {Γ′ +s -s r} → Stmt Γ Γ′ +s -s r → Stmt Γ Γ +s -s r
  _∙_ : ∀ {Γ′ Γ″ +s -s r₁ r₂} → Stmt Γ Γ′ +s -s r₁ → Stmt Γ′ Γ″ +s -s r₂ → Stmt Γ Γ″ +s -s (r₁ ∨ r₂)

infixl 6 _*_ _/_
infixl 5 _+_ _-_
infix 4 _==_ _!=_ _<_ _>_ _<=_ _>=_
infix 2 _&&_
infix 1 _||_

data Expr Γ +s where
  # : ℤ → Expr Γ +s false integer
  - : ∀ {r} → Expr Γ +s r integer → Expr Γ +s r integer
  _+_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) integer
  _-_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) integer
  _*_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) integer
  _/_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) integer
  true : Expr Γ +s false boolean
  false : Expr Γ +s false boolean
  ! : ∀ {r} → Expr Γ +s r boolean → Expr Γ +s r boolean
  _&&_ : ∀ {r₁ r₂} → Expr Γ +s r₁ boolean → Expr Γ +s r₂ boolean → Expr Γ +s (r₁ ∨ r₂) boolean
  _||_ : ∀ {r₁ r₂} → Expr Γ +s r₁ boolean → Expr Γ +s r₂ boolean → Expr Γ +s (r₁ ∨ r₂) boolean
  _==_ : ∀ {a r₁ r₂} → Expr Γ +s r₁ a → Expr Γ +s r₂ a → Expr Γ +s (r₁ ∨ r₂) boolean
  _!=_ : ∀ {a r₁ r₂} → Expr Γ +s r₁ a → Expr Γ +s r₂ a → Expr Γ +s (r₁ ∨ r₂) boolean
  _<_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) boolean
  _>_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) boolean
  _<=_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) boolean
  _>=_ : ∀ {r₁ r₂} → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) boolean
  [_yields_] : ∀ {Γ′ -s r₁ r₂} → Stmt Γ Γ′ +s -s r₁ → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) integer
  var : ∀ {a } → Ref (decl a expr) Γ → Expr Γ +s false a
  var_[_] : ∀ {a r} → Ref (decl a array1D) Γ → Expr Γ +s r integer → Expr Γ +s r a
  var_[_,_] : ∀ {a r₁ r₂} → Ref (decl a array2D) Γ → Expr Γ +s r₁ integer → Expr Γ +s r₂ integer → Expr Γ +s (r₁ ∨ r₂) a
  call : ∀ {a r xs} → Ref (function-type a xs) Γ → tuplefy Γ +s r xs → Expr Γ +s r a

test : Stmt [] [] top-scope no-scope false
test =
  begin
    def integer [ expr ] ∙
    def integer [ array1D ] ∙
    var z [ var (s z) ]:= (var (s z)) ∙

    procedure [ integer ] begin
      return
    end ∙

    put (out-string "hello world: " ∷ₒ out-expr (var (s (s z))) ∷ₒ []ₒ) ∙
    get [ s (s z) ] ∙
    put (out-skip ∷ₒ []ₒ) ∙
  
    def boolean [ array1D ] ∙
    function boolean [ boolean ] begin
      call (s (s z)) (var (s (s (s (s z)))) , tt) ∙
      return-[ var z ]
    end
  end
