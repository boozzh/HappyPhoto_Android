package cn.happyz.happyphoto.DataProvider.Activity;

/**
 * Created by zcmzc on 14-1-19.
 */
public class ActivityUser {
    private int ActivityId;
    private int UserId;
    private int State;

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }
}
