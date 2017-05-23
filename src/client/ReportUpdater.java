package client;

import java.util.Hashtable;

import GUI.controllers.MainWindowController;
import javafx.application.Platform;
import util.Report;

class ReportUpdater extends Thread {

	private enum Operation { ADD, UPDATE, DELETE }
	
	private MainWindowController view;
	private Hashtable<Report, Operation> buffer;
	
	
	
	public ReportUpdater(MainWindowController view) {
		this.view = view;
		this.buffer = new Hashtable<Report, Operation>();
	}
	
	
	
	@Override
	public void run() {
		while(true) {
			
			if(isInterrupted())
				return;
			
			try {
				synchronized(buffer) {
					if(buffer.size() > 0) {
						for(Report report : buffer.keySet()) {
							Operation op = buffer.remove(report);
							performOperation(report, op);
						}
					}
					buffer.wait();	
				}
				
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	public void putAddOperation(Report rep) {
		synchronized(buffer) {
			buffer.put(rep, Operation.ADD);
			buffer.notify();
		}
	}
	
	public void putUpdateOperation(Report rep) {
		synchronized(buffer) {
			buffer.put(rep, Operation.UPDATE);
			buffer.notify();
		}
	}
	
	public void putDeleteOperation(Report rep) {
		synchronized(buffer) {
			buffer.put(rep, Operation.DELETE);
			buffer.notify();
		}
	}

	private void performOperation(Report report, Operation op) {
		switch(op) {
		case ADD:
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					view.addReport(report);
					
				}
			});
			
			break;
			
		case UPDATE:
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					view.updateReport(report);
				}
				
			});
			
			break;
			
		case DELETE:
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					view.deleteReport(report);
				}
				
			});

			break;
			
		}
		
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
		buffer.notify();
	}
}
