package com.maikoid.pomotron;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.maikoid.pomotron.controller.SinglePomodoroExecuter;
import com.maikoid.pomotron.controller.SinglePomodoroExecuter.PomodoroType;

public class TestPomodoroExecuter {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void Test_StartStopPomodoroTypes_S_ThrowNoError(){				
		SinglePomodoroExecuter pe = new SinglePomodoroExecuter(PomodoroType.WORK);		
		pe.start();		
		pe.end();
	}
	
	@Test
	public void Test_DoubleOperationPomodoro_S_ThrowError(){				
		SinglePomodoroExecuter pe = new SinglePomodoroExecuter(PomodoroType.SHORT_BREAK);		
		pe.start();		
		thrown.expect(IllegalStateException.class);
		pe.start();
		pe.end();
		pe.end();
	}
	
	@Test
	public void Test_StartEndedPomodoro_S_ThrowError(){				
		SinglePomodoroExecuter pe = new SinglePomodoroExecuter(PomodoroType.LONG_BREAK);		
		pe.start();		
		pe.end();
		thrown.expect(IllegalStateException.class);
		pe.start();
	}

}
