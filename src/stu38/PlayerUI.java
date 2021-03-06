package stu38;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jvnet.substance.skin.*;

import java.io.File;
import java.util.List;

import mp3x.ctl.*;
import stu38.MusicInfo;
import stu38.PlayerControl;

public class PlayerUI extends JFrame{

	
	private JSlider processSlider;
	private JSlider volumnSlider;
	private JButton playButton;
	private JButton stopButton;
	private JButton upButton;
	private JButton downButton;
	private JButton addButton;
	private JButton deleteButton;
	private JList list;
	private JLabel l_name;
	private JPanel back;
	private JLabel [] l_texts = new JLabel[9];
	private JLabel [] l_text = new JLabel[3];
	private JLabel l_time;
	private JCheckBox checkBox;
	private JCheckBox treanBox;
	private JCheckBox muteCheckBox;

	
	private boolean progressSliderDrag = false;
	private DefaultListModel model;
	private PlayerControl control;
	private List<MusicInfo> lists;
	private MusicInfo currentMp3;
	private static int crrentList = 0;
	private Mp3TVShow  mp3TVShow = new Mp3TVShow();
	private Mp3TVShow  mp3TVShow1 = new Mp3TVShow();
	private List<Long> times;
	private List<String> messages;
	
	private Timer progressTime;
	private Timer nameTime;
	private Timer textTime;
	private static int currentValue = 0;
	private static long currentTime = 0;
	private static int L_WIDTH = 40;
	private static int currentLocation = 4;
	private static int currentLoca = 1;
	
	private boolean tranType = false;	//����ѭ��ʹ��
	
	public enum Mp3Status{	//ö��
		PLAY,PAUSE,STOP
	}
	private Mp3Status currentStatus = Mp3Status.STOP;
	
	public PlayerUI(){
		setSize(760,630);
		setLayout(null);
		initCompent();
		setVisible(true);
		setTitle("38 - Ҷ�Ŀ� - Jolita Music!");
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	private void initCompent(){
		//��������
		processSlider = getProcessSlider();
		this.add(processSlider);
		playButton = getPlayButton();
		this.add(playButton);
		stopButton = getStopButton();
		this.add(stopButton);
		upButton = getUpButton();
		this.add(upButton);
		downButton = getDownButton();
		this.add(downButton);
		addButton = getAddButton();
		this.add(addButton);
		deleteButton = getDeleteButton();
		this.add(deleteButton);
		list = getList();
		this.add(list);
		//����Ƶ��
		checkBox = getCheck();
		this.add(checkBox);
		//����
		volumnSlider = getVolumnSlider();
		this.add(volumnSlider);
		muteCheckBox = getMuteCheckBox();
		this.add(muteCheckBox);
		//����ѭ��ģʽ
		treanBox = getTranBox();
		this.add(treanBox);
		//��ʾʱ��-�����
		l_time = new JLabel();
		l_time.setBounds(293, 40, 40, 20);
		l_time.setForeground(Color.black);
		l_time.setText("00:00");
		this.add(l_time);
		//Ƶ��
		mp3TVShow.setBounds(360, 100, 350, 270);
		this.add(mp3TVShow);
		mp3TVShow1.setBounds(350, 80, 1, 1);
		this.add(mp3TVShow1);
		
		l_name = getNameLabel();
		this.add(l_name);
		//���
		for(int i = 0; i<3; i++){
			l_text[i] = new JLabel();
			l_text[i].setHorizontalAlignment(SwingConstants.CENTER);
//			l_text[i].setText("��ʿ����Բ���");
			l_text[i].setBounds(360,400+ i * L_WIDTH,330,20);
			this.add(l_text[i]);
		}
		
		
		control = new PlayerControl();
		nameTime = new Timer(100,new TextListener(l_name));
		nameTime.start();
		progressTime = new Timer(1000,new MyListener(processSlider));
		textTime = new Timer(10,new LrcListener());
	}
	
	private JCheckBox getCheck(){
		if(checkBox==null){
			checkBox = new JCheckBox();
			checkBox.setText("����Ƶ��");
			checkBox.setBounds(600, 70, 200, 20);
			checkBox.addItemListener(new ItemListener(){

				public void itemStateChanged(ItemEvent e) {
					if(checkBox.isSelected()){
						back.setVisible(true);
						PlayerUI.this.remove(mp3TVShow);
						PlayerUI.this.repaint();
						l_text[0].setVisible(false);
						l_text[1].setVisible(false);
						l_text[2].setVisible(false);
					}else{
						back.setVisible(false);
						PlayerUI.this.add(mp3TVShow);
						PlayerUI.this.repaint();
						//currentMp3.getPlayer().setTVShow(mp3TVShow);
						l_text[0].setVisible(true);
						l_text[1].setVisible(true);
						l_text[2].setVisible(true);
					}
				}
				
			});
		}
		return checkBox;
	}
	private void initBack(){
		//���Ӹ����ʾ
		for(int i=0;i<9;i++){
			l_texts[i] = new JLabel();
			l_texts[i].setHorizontalAlignment(SwingConstants.CENTER);
			l_texts[i].setBounds(50, 20 + i * L_WIDTH, 200, 20);
			//l_texts[i].setText("hello world");
			back.add(l_texts[i]);
		}
		
		
	}
	private JLabel getNameLabel(){
		if(l_name == null){
			l_name = new JLabel();
			l_name.setBounds(150, 25, 100, 20);
			//l_name.setText("hello world");
			l_name.setFont(new java.awt.Font("Dialog",0,14));
			l_name.setForeground(Color.orange);
		}
		return l_name;
	}
	private JSlider getProcessSlider() {
		if (processSlider == null) {
			processSlider = new JSlider();
			processSlider.setBounds(10, 40, 280, 20);
			processSlider.setValue(0);
			processSlider.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					textTime.stop();
					if(!processSlider.getValueIsAdjusting()){
						
						if(progressSliderDrag){			
							progressSliderDrag = false;
							double rate = processSlider.getValue()*1.0/processSlider.getMaximum();
							currentValue = processSlider.getValue();
							currentTime = (long) (currentMp3.getPlayer().getTotalTimeSecond()*1000 * rate);
							currentMp3.getPlayer().seek(rate);
							
						}
					}else{
						progressSliderDrag = true;
					}
					textTime.start();
				}
				
			});
		}
		return processSlider;
	}
	private JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton();
			playButton.setBounds(150, 65,60, 20);
			playButton.setText("����");
			playButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {				
					dealPlayButton();
					
				}

			});
		}
		return playButton;
	}
	private void dealPlayButton(){
		//currentMp3.getPlayer().setTVShow(mp3TVShow);
		if(currentStatus.equals(Mp3Status.PLAY)&&currentMp3!=null){
			currentMp3.pause();
			progressTime.stop();
			textTime.stop();
			currentStatus = Mp3Status.PAUSE;
			playButton.setText("����");
		}else if(currentStatus.equals(Mp3Status.PAUSE)&&currentMp3!=null){
			currentMp3.goon();
			progressTime.start();
			textTime.start();
			currentStatus = Mp3Status.PLAY;
			playButton.setText("��ͣ");
		}else if(currentStatus.equals(Mp3Status.STOP)){
			playCurrentSong();
			currentStatus = Mp3Status.PLAY;
			playButton.setText("��ͣ");
		}
	}
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setBounds(20, 65,60, 20);
			stopButton.setText("ֹͣ");
			stopButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {				
				    currentMp3.stop();
				    progressTime.stop();
				    playButton.setText("����");
				    currentStatus = Mp3Status.STOP;
				}

			});
		}
		return stopButton;
	}
	private JButton getUpButton() {
		if (upButton == null) {
			upButton = new JButton();
			upButton.setBounds(85, 65,60, 20);
			upButton.setText("��һ��");
			upButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {				
				    int currentIndex = list.getSelectedIndex();
				    if(currentIndex>0){
				    	list.setSelectedIndex(currentIndex-1);
				    }else{
				    	
				    }
				    playCurrentSong();
				}

			});
		}
		return upButton;
	}
	private JButton getDownButton() {
		if (downButton == null) {
			downButton = new JButton();
			downButton.setBounds(220, 65,60, 20);
			downButton.setText("��һ��");
			downButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {	
					int currentIndex = list.getSelectedIndex();
				    if(currentIndex<(lists.size()-1)){
				    	currentMp3 = lists.get(currentIndex+1);
				    	list.setSelectedIndex(currentIndex+1);
				    }else{
				    	
				    }
				    playCurrentSong();
				}

			});
		}
		return downButton;
	}
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setBounds(60, 530,60, 20);
			addButton.setText("���Ӹ���");
			addButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {				
					loadFilesByJF();
				}

			});
		}
		return addButton;
	}
	private JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setBounds(160, 530,60, 20);
			deleteButton.setText("ɾ������");
			deleteButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {
					  
			          int index = list.getSelectedIndex();
			          if(index >= 0){
			        	  lists.remove(index);
				          model.removeAllElements();
							for(int i=0;i<lists.size();i++){
								model.add(i, removeIndex(lists.get(i).getFile().getName()));
							}
							list.revalidate();
			          }
			          
				}

			});
		}
		return deleteButton;
	}
	private JSlider getVolumnSlider() {
		if (volumnSlider == null) {
			volumnSlider = new JSlider();
			volumnSlider.setBounds(200,90,80,20);
			volumnSlider.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					setVolumn();
				}
				
			});
		}
		return volumnSlider;
	}
	private JCheckBox getTranBox(){
		if(treanBox == null){
			treanBox = new JCheckBox();
			treanBox.setBounds(20,90,100,21);
			treanBox.setText("����ѭ��");
			treanBox.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					if(treanBox.isSelected()){
						tranType = true;
					}else{
						tranType = false;
					}
				}
				
			});
		}
		return treanBox;
	}
	private JCheckBox getMuteCheckBox() {
		if (muteCheckBox == null) {
			muteCheckBox = new JCheckBox();
			muteCheckBox.setBounds(280,90,60,21);
			muteCheckBox.setText("����");
			muteCheckBox.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					if(muteCheckBox.isSelected()){
						getVolumnSlider().setEnabled(false);
					}else{						
						getVolumnSlider().setEnabled(true);
					}
					setVolumn();
				}
				
			});
		}
		return muteCheckBox;
	}

	private void setVolumn(){
		if(currentMp3!=null){
			if(getVolumnSlider().isEnabled()){			
				double gain = getVolumnSlider().getValue()*1.0/getVolumnSlider().getMaximum();
				currentMp3.getPlayer().setVolumnGain(gain);
			}else{
				currentMp3.getPlayer().setVolumnGain(0);
			}
		}
	}

	private JList<String> getList(){
		model = new DefaultListModel();
		
		if(list==null){
			list = new JList<String>();
			list.setModel(model);
			list.setBounds(15, 120, 300, 400);
			list.setBackground(Color.white);
			list.setFixedCellHeight(20);
			list.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2){
						dealPlay();
					}
				}
			});
		}
		return list;
	}
	
	private void dealPlay(){		//��ʼ����
		playButton.setText("��ͣ");	//����
		currentStatus = Mp3Status.PLAY;
		playCurrentSong();
	}
	
	private void playCurrentSong(){		//���ŵ�ǰ����
		if(currentMp3!=null){
			currentMp3.stop();
			currentMp3=null;
		}
		
		if(list.getSelectedIndex()>=0){
			crrentList = list.getSelectedIndex();
			currentMp3 = lists.get(list.getSelectedIndex());
			currentMp3.getPlayer().setTVShow(mp3TVShow);
			currentMp3.on();
			l_name.setText("");
			l_name.setText(removeIndex(currentMp3.getFile().getName()));
			//����������
			currentValue = 0;
			processSlider.setMaximum((int)currentMp3.getPlayer().getTotalTimeSecond());
			progressTime.start();
			//���Ӹ�����
			if(back!=null){
				back.removeAll();
				this.remove(back);
				this.repaint();
				back = null;
			}
			
			back.setBounds(370, 120, 330, 400);
			//back.setLayout(null);
			back.setOpaque(false);
			//back.setBackground(Color.red);
			initBack();
			if(checkBox.isSelected()){
				back.setVisible(true);
			}else{
				back.setVisible(false);
			}
			
			this.getContentPane().add(back);
			
			this.repaint();
			
			//�������
			if(textTime!=null){
				textTime.stop();
			}
			for(int index = 0; index < 9; index++){
				l_texts[index].setText("");
			}
			for(int ind = 0; ind<3;ind++){
				l_text[ind].setText("");
			}
			if(currentMp3.getMessages().size()>1){
				
				times = currentMp3.getTimeMills();
				
				messages = currentMp3.getMessages();
				currentTime = 0;
				textTime.start();
				
			}else{
				l_text[1].setText("�ø������޸��");
				l_texts[currentLocation].setText("�ø������޸��");
			}
			
		}
		
	}
	private void loadFilesByJF(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = chooser.getSelectedFiles();
			control.add(selectedFiles);
			lists = control.getMpLists();
			model.removeAllElements();
			for(int i=0;i<lists.size();i++){
				model.add(i, removeIndex(lists.get(i).getFile().getName()));
			}
			list.revalidate();
			list.setSelectedIndex(0);
			if(currentMp3==null){
				currentMp3 = lists.get(list.getSelectedIndex());
			}
		}

	}
	private String removeIndex(String name){
		int ind = name.indexOf('.');
		String child = name.substring(0, ind);
		return child;
	}
	class TextListener implements ActionListener{

		private JLabel label;
		int index = 2;
		public TextListener(JLabel label){
			this.label = label;
			
		}

		public void actionPerformed(ActionEvent e) {
			label.setBounds(100+index,22,150,20);
			index = index +3;
			
			if(index > 150){
				index = 3;
			}
		}
		
	}
	class MyListener implements ActionListener{

		private JSlider lider;
		public MyListener(JSlider lider){
			this.lider = lider;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			lider.setValue(currentValue++);
			int minite = currentValue/60;
			int seconds = currentValue%60;
			String second = "";
			if(seconds>=10){
				second = seconds+"";
			}else{
				second = "0"+seconds;
			}
			l_time.setText("0"+minite+":"+second);
			if(lider.getValue() >= lider.getMaximum()-1){
				//int currentIndex = list.getSelectedIndex();
				if(tranType){
					list.setSelectedIndex(crrentList);
				}else{
					if(crrentList<(lists.size()-1)){
				    	//currentMp3 = lists.get(currentIndex+1);
				    	list.setSelectedIndex(crrentList+1);
				    }else{
				    	list.setSelectedIndex(0);
				    }
				}
			    
			    playCurrentSong();
			}
		}
		
	}
	class LrcListener implements ActionListener{

		private int i = 0;
		private long nextMill = 0;
		private String message = "";
		public void actionPerformed(ActionEvent e) {
			
			System.out.println(currentMp3.getTimeMills());
			System.out.println(currentMp3.getMessages().size());
			
			for(int j=0;j<times.size();j++){
				nextMill = times.get(j);
				if(currentTime < nextMill){
					nextMill = times.get(j+1);
					i = j + 1;
					break;
				}
			}
			
			currentTime = currentTime + 10;
			if(currentTime>nextMill){
				if(message.equals(messages.get(i))){
				}else{
					
					for(int a=0;a<times.size();a++){
					}
					message = messages.get(i);
					System.out.println(message);
					for(int index=0; index < 5;index++){
				        if(index == 0){
							l_texts[(currentLocation + index)%9].setFont(new java.awt.Font("Dialog",0,22));
							l_texts[(currentLocation + index)%9].setForeground(Color.red);
							l_texts[(currentLocation + index)%9].setText(messages.get(i + index));
							l_text[currentLoca + index].setFont(new java.awt.Font("Dialog",0,22));
							l_text[currentLoca + index].setForeground(Color.red);
							l_text[currentLoca + index].setText(messages.get(i + index));
							if(i>0){
								l_text[0].setFont(new java.awt.Font("Dialog",0,16));
								l_text[0].setForeground(Color.orange);
								l_text[0].setText(messages.get(i - 1));
							}
							if(i<(messages.size()-1)){
								l_text[2].setFont(new java.awt.Font("Dialog",0,16));
								l_text[2].setForeground(Color.orange);
								l_text[2].setText(messages.get(i + 1));
							}
							
						}else {
							if(i+index<messages.size()){
								l_texts[(currentLocation + index)%9].setFont(new java.awt.Font("Dialog",0,16));
								l_texts[(currentLocation + index)%9].setForeground(Color.orange);
								l_texts[(currentLocation + index)%9].setText(messages.get(i + index));
							}else{
								l_texts[(currentLocation + index)%9].setText("");
							}
							
						}
					}
					for(int c = 1;c<5;c++){
						if(c-1<i){
							if(currentLocation - c>=0){
								l_texts[currentLocation - c].setFont(new java.awt.Font("Dialog",0,16));
								l_texts[currentLocation - c].setForeground(Color.orange);
								l_texts[currentLocation - c].setText(messages.get(i -c));
							}else{
								l_texts[currentLocation - c + 9].setFont(new java.awt.Font("Dialog",0,16));
								l_texts[currentLocation - c + 9].setForeground(Color.orange);
								l_texts[currentLocation - c + 9].setText(messages.get(i -c));
							}
						}
						
					}
					for(int je = 0;je<9;je++){
						if(currentLocation >= 4){
							l_texts[(currentLocation-4 + je)%9].setBounds(20, 20 + je * L_WIDTH, 270, 20);
						}else {
							l_texts[(currentLocation + 9 -4 + je)%9].setBounds(20, 20 + je * L_WIDTH, 270, 20);
						}
					}
				}
                 
			}
			
		}
		
	}
}
