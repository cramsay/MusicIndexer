
public interface ProgressListener {
	
	//Progress Bar Update Methods
	public void setOverallProgress(int val);
	public void setOverallMax(int val);
	public void setCurrentProgress(int val);
	public void setCurrentMax(int val);
	
	//Detail Publishers
	public void addInfo(String msg);
	public void addWarning(String msg);
	public void clearInfo();
	public void clearWarning();
	public void setTaskName(String msg);
	
	//Finished Notification
	public void done();
}
