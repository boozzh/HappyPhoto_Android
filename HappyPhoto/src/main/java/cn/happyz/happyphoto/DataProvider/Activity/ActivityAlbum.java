package cn.happyz.happyphoto.DataProvider.Activity;

/**
 * Created by zcmzc on 14-1-19.
 */
public class ActivityAlbum {
    private int UserAlbumId;
    private int ActivityId;
    private int ActivityRound;
    private int ActivitySubRound;

    public int getUserAlbumId() {
        return UserAlbumId;
    }

    public void setUserAlbumId(int userAlbumId) {
        UserAlbumId = userAlbumId;
    }

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public int getActivityRound() {
        return ActivityRound;
    }

    public void setActivityRound(int activityRound) {
        ActivityRound = activityRound;
    }

    public int getActivitySubRound() {
        return ActivitySubRound;
    }

    public void setActivitySubRound(int activitySubRound) {
        ActivitySubRound = activitySubRound;
    }
}
