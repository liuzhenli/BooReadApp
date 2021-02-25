package com.liuzhenli.reader.network;


import com.liuzhenli.reader.module.ApiModule;
import com.liuzhenli.reader.module.AppModule;
import com.liuzhenli.reader.ui.activity.ChangeSourceActivity;
import com.liuzhenli.reader.ui.activity.BookDetailActivity;
import com.liuzhenli.reader.ui.activity.BookSourceActivity;
import com.liuzhenli.reader.ui.activity.DatabaseTableListActivity;
import com.liuzhenli.reader.ui.activity.DownloadActivity;
import com.liuzhenli.reader.ui.activity.EditSourceActivity;
import com.liuzhenli.reader.ui.activity.ImportLocalBookActivity;
import com.liuzhenli.reader.ui.activity.LoginActivity;
import com.liuzhenli.reader.ui.activity.ReaderActivity;
import com.liuzhenli.reader.ui.activity.SearchActivity;
import com.liuzhenli.reader.ui.activity.SettingActivity;
import com.liuzhenli.reader.ui.activity.TestSourceActivity;
import com.liuzhenli.reader.ui.fragment.BookCategoryFragment;
import com.liuzhenli.reader.ui.fragment.BookShelfFragment;
import com.liuzhenli.reader.ui.fragment.DiscoverFragment;
import com.liuzhenli.reader.ui.fragment.LocalFileFragment;
import com.liuzhenli.reader.ui.fragment.LocalTxtFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Liuzhenli
 * @since 19/7/6
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {

    void inject(LoginActivity loginActivity);

    void inject(LocalFileFragment localFileFragment);

    void inject(ImportLocalBookActivity importLocalBookActivity);

    void inject(BookShelfFragment bookShelfFragment);

    void inject(ReaderActivity readActivity);

    void inject(ChangeSourceActivity bookCatalogActivity);

    void inject(LocalTxtFragment localTxtFragment);

    void inject(AppComponent appComponent);

    void inject(DiscoverFragment discoverFragment);

    void inject(BookCategoryFragment bookCategoryFragment);

    void inject(BookDetailActivity bookDetailActivity);

    void inject(SearchActivity searchActivity);

    void inject(BookSourceActivity bookSourceActivity);

    void inject(EditSourceActivity editSourceActivity);

    void inject(TestSourceActivity testSourceActivity);

    void inject(SettingActivity settingActivity);

    void inject(DownloadActivity downloadActivity);

    void inject(DatabaseTableListActivity databaseTableListActivity);

}
