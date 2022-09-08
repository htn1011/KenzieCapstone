package com.kenzie.capstone.service.dependency;

import com.kenzie.capstone.service.UserService;
import com.kenzie.capstone.service.dao.UserDao;

import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
    includes = DaoModule.class
)
public class ServiceModule {

    @Singleton
    @Provides
    @Inject
    public UserService provideLambdaService(@Named("ExampleDao") UserDao userDao) {
        return new UserService(userDao);
    }
}

