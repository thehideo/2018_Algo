public class ActionPatrol extends ActionControlAbstract {
	@Override
	public void action() {
		GroupManager.instance().updateGroupTarget();
	}
}
