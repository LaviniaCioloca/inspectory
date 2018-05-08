package edu.lavinia.inspectory.inspection;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class CommandsTest {
	private static Commands commands;

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Test
	public void testGenerateOptions() {
		final String[] args = { "-c" };
		commands = new Commands(args, Optional.empty());
	}

	@Test
	public void testHelpCommand() {
		exit.expectSystemExitWithStatus(0);

		final String[] args = { "-h" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}

	@Test
	public void testCleanCommand() {
		final String[] args = { "-c" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}

	@Test(expected = NoSuchElementException.class)
	public void testAllCommand() {
		final String[] args = { "-all" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}

	@Test(expected = NoSuchElementException.class)
	public void testAMMCommand() {
		final String[] args = { "-amm" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}

	@Test(expected = NoSuchElementException.class)
	public void testOPMCommand() {
		final String[] args = { "-opm" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}

	@Test
	public void testErrorCommand() {
		exit.expectSystemExitWithStatus(0);

		final String[] args = { "" };
		commands = new Commands(args, Optional.empty());

		commands.parse();
	}
}
