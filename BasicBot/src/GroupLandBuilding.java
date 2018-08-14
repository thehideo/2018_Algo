public class GroupLandBuilding extends GroupAbstract {

	@Override
	void action() {
		targetPosition = MyUtil.getSaveTilePosition(8).toPosition();
	}

}
