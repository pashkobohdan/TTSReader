package com.pashkobohdan.ttsreader.ui.common;

import android.support.v4.app.FragmentManager;

import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter;

import ru.terrakok.cicerone.android.SupportFragmentNavigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

public abstract class CustomFragmentNavigator extends SupportFragmentNavigator {
    private FragmentManager fragmentManager;
    private int containerId;

    public CustomFragmentNavigator(FragmentManager fragmentManager, int containerId) {
        super(fragmentManager, containerId);
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    @Override
    public void applyCommand(Command command) {
        if (command instanceof Forward) {
            Forward forward = (Forward) command;
            String screen = forward.getScreenKey();
            AbstractPresenter.currentScreen = screen;
            fragmentManager
                    .beginTransaction()
                    .replace(containerId, createFragment(screen, forward.getTransitionData()))
                    .addToBackStack(screen)
                    .commit();
        } else if (command instanceof Back) {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            } else {
                exit();
            }
        } else if (command instanceof Replace) {
            Replace replace = (Replace) command;
            if (fragmentManager.getBackStackEntryCount() > 0) {
                String screen = replace.getScreenKey();
                AbstractPresenter.currentScreen = screen;
                fragmentManager.popBackStackImmediate();
                fragmentManager
                        .beginTransaction()
                        .replace(containerId, createFragment(screen, replace.getTransitionData()))
                        .addToBackStack(screen)
                        .commit();
            } else {
                String screen = replace.getScreenKey();
                AbstractPresenter.currentScreen = screen;
                fragmentManager
                        .beginTransaction()
                        .replace(containerId, createFragment(screen, replace.getTransitionData()))
                        .commit();
            }
        } else if (command instanceof BackTo) {
            String key = ((BackTo) command).getScreenKey();
            AbstractPresenter.currentScreen = key;

            if (key == null) {
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                    fragmentManager.popBackStack();
                }
                fragmentManager.executePendingTransactions();
            } else {
                boolean hasScreen = false;
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                    if (key.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
                        fragmentManager.popBackStackImmediate(key, 0);
                        hasScreen = true;
                        break;
                    }
                }
                if (!hasScreen) {
                    backToUnexisting();
                }
            }
        } else if (command instanceof SystemMessage) {
            showSystemMessage(((SystemMessage) command).getMessage());
        }
    }
}
