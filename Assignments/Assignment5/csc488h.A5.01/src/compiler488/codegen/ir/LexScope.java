package compiler488.codegen.ir;

import java.util.ArrayList;
import java.util.HashMap;

interface LexScope {
	/* lexscope for IR generator */
	void addLocal(String name, Entity entity);
	void addRefer(String name, Entity entity);
	int getEmbedSize();
	int getLexLevel();
	void fixOffset(int base);
	void embed(LexScope scope);
	Entity resolve(String name);

	/* use outer scope to do every thing */
	public static class ProxyScope implements LexScope {
		private ArrayList<LexScope> embeds;
		private LexScope outer;
		public ProxyScope(LexScope outer_) {
			outer = outer_;
			embeds = new ArrayList<LexScope>();
		}
		public void addLocal(String name, Entity entity) {
			outer.addLocal(name, entity);
		}
		public void addRefer(String name, Entity entity) {
			outer.addRefer(name, entity);
		}
		public int getEmbedSize() {
			int maxsize = 0;
			for(LexScope scope: embeds) {
				int t = scope.getEmbedSize();
				if(t > maxsize)
					maxsize = t;
			}
			return maxsize;
		}
		public int getLexLevel() { return outer.getLexLevel(); }
		public void fixOffset(int base) {
			for(LexScope scope: embeds)
				scope.fixOffset(base);
		}
		public void embed(LexScope scope) { embeds.add(scope); }
		public Entity resolve(String name) {
			return outer.resolve(name);
		}
	}

	// minor scope can have local variables
	// so we do some minor updates
	public static class MinorScope extends ProxyScope {
		HashMap<String, Entity> entities;
		ArrayList<Address.StackAddress> fixes;
		int localsize;
		public MinorScope(LexScope outer_) {
			super(outer_);
			entities = new HashMap<String, Entity>();
			fixes = new ArrayList<Address.StackAddress>();
			localsize = 0;
		}
		public void addLocal(String name, Entity entity) {
			Address.StackAddress addr =
				new Address.StackAddress(Cell.scalar(getLexLevel()), localsize);
			localsize += entity.getSize();
			fixes.add(addr);
			entity.setAddress(addr);
			// FIXME check first
			entities.put(name, entity);
		}
		public void addRefer(String name, Entity entity) {
			entities.put(name, entity);
		}
		public int getEmbedSize() {
			return localsize + super.getEmbedSize();
		}
		public void fixOffset(int base) {
			for(Address.StackAddress c : fixes)
				c.fixOffset(base);
			super.fixOffset(base + localsize);
		}
		public Entity resolve(String name) {
			Entity entity = entities.get(name);
			if(entity != null)
				return entity;
			else
				return super.resolve(name);
		}
	}

	// major scope does not embed in other scope
	public static class MajorScope extends MinorScope {
		private int lexlevel;
		public MajorScope(LexScope outer_) {
			super(outer_);
			lexlevel = (outer_!=null ? outer_.getLexLevel() + 1 : 0);
		}

		public int getEmbedSize() {
			throw new RuntimeException("MajorScope can not be embedded in another scope");
		}
		public int getStackSize() { return super.getEmbedSize(); }
		public int getLexLevel() { return lexlevel; }
		public void fixOffset(int base) {
			throw new RuntimeException("MajorScope can not be embedded in another scope");
		}
		public void fixOffset() { super.fixOffset(0); }
	};
};