package cn.happyz.happyphoto.DataProvider.Activity;

/**
 * Created by zcmzc on 14-2-5.
 */
public class ActivityVoteRecord {
    private int ActivityVoteRecordId;
    private int ActivityId;
    private int ActivityRound;
    private int UserAlbumId;
    private int UserId;
    private int VoteCount;
    private String CreateDate;

    public int getActivityVoteRecordId() {
        return ActivityVoteRecordId;
    }

    public void setActivityVoteRecordId(int activityVoteRecordId) {
        ActivityVoteRecordId = activityVoteRecordId;
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

    public int getUserAlbumId() {
        return UserAlbumId;
    }

    public void setUserAlbumId(int userAlbumId) {
        UserAlbumId = userAlbumId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getVoteCount() {
        return VoteCount;
    }

    public void setVoteCount(int voteCount) {
        VoteCount = voteCount;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
