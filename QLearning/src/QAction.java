import java.io.Serializable;
import java.util.Objects;

/**
 * Action 객체
 * @author SDS
 *
 */
public class QAction implements Serializable {

	private static final long serialVersionUID = -8206823327625930748L;
	
	private String actionType; // 공격, 이동, 클로킹
	private int targetId; // 공격대상유닛id
	private String targetUnitType; // 공격대상 유닛 타입
	private int moveGrdIdx; // 이동하려는 그리드 격자
	private int moveX; // 이동하려는 그리드 격자의 중앙 포지션 X
	private int moveY; // 이동하려는 그리드 격자의 중앙 포지션 Y
	
	/**
	 * ActionType을 리턴한다
	 * Attack, Move
	 * @return actionType
	 */
	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	/**
	 * 공격해야할 적유닛의 유닛ID를 리턴
	 * @return
	 */
	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	
	public String getTargetUnitType() {
		return targetUnitType;
	}
	
	public void setTargetUnitType(String targetUnitType) {
		this.targetUnitType = targetUnitType;
	}

	/**
	 * 이동해야할 그리드 격자의 인덱스를 리턴
	 * @return
	 */
	public int getMoveGrdIdx() {
		return moveGrdIdx;
	}

	public void setMoveGrdIdx(int moveGrdIdx) {
		this.moveGrdIdx = moveGrdIdx;
	}

	public int getMoveX() {
		return moveX;
	}

	public void setMoveX(int moveX) {
		this.moveX = moveX;
	}

	public int getMoveY() {
		return moveY;
	}

	public void setMoveY(int moveY) {
		this.moveY = moveY;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof QAction)) {
            return false;
        }
        QAction qActionObj = (QAction) obj;
        
        return Objects.equals(this.actionType, qActionObj.actionType) && Objects.equals(this.targetUnitType, qActionObj.targetUnitType) && this.moveGrdIdx == qActionObj.moveGrdIdx;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(actionType, targetUnitType, moveGrdIdx);
	}
}
