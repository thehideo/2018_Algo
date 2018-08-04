public class ActionControlGroup extends ActionControlAbstract {
	@Override
	public void action() {
		GroupManager.instance().updateGroupTarget();
		GroupManager.instance().fillGroup();
	}
}
