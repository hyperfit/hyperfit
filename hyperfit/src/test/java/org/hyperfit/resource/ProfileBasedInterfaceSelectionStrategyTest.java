package org.hyperfit.resource;

import org.hamcrest.Matchers;
import org.hyperfit.annotation.Profiles;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

public class ProfileBasedInterfaceSelectionStrategyTest {

    public interface BaseProfileResource extends HyperResource {

    }

    @Profiles("/a/b/c/profile-resource-1")
    public interface ProfileResource1 extends BaseProfileResource {
    }

    @Profiles("/a/b/c/profile-resource-2")
    public interface ProfileResource2 extends BaseProfileResource {
    }

    @Profiles("/a/b/c/profile-resource-3")
    public interface ProfileResource3A extends BaseProfileResource {
    }

    @Profiles("/a/b/c/profile-resource-3")
    public interface ProfileResource3B extends BaseProfileResource {
    }

    @Profiles({"/a/b/c/multiple-profile-resource-a", "/a/b/c/multiple-profile-resource-b"})
    public interface MultipleProfileResource extends BaseProfileResource {
    }

    @Profiles("/a/b/c/not-in-registry-resource")
    public interface NotInRegistryResource extends BaseProfileResource {

    }

    private static Collection<Class<? extends HyperResource>> interfaces = Arrays.<Class<? extends HyperResource>>asList(
        ProfileResource1.class,
        ProfileResource2.class,
        MultipleProfileResource.class,
        ProfileResource3A.class,
        ProfileResource3B.class
    );


    @Test
    public void testDetermineInterfacesNothingRegistered(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(Collections.<Class<? extends HyperResource>>emptyList());

        HyperResource mockResource = mock(HyperResource.class);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);

        assertArrayEquals(new Class<?>[]{HyperResource.class}, result);

    }


    @Test
    public void testDetermineInterfacesNoProfiles(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        HyperResource mockResource = mock(HyperResource.class);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);

        assertArrayEquals(new Class<?>[]{HyperResource.class}, result);

    }


    @Test
    public void testDetermineInterfacesSecondProfile(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        //second profile link in array of profiles for MultipleProfileResource
        profiles.add("/a/b/c/multiple-profile-resource-b");

        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
            .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);

        assertThat(
            "matching second profile in array should work",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(HyperResource.class, MultipleProfileResource.class)
        );
    }


    @Test
    public void testDetermineInterfacesTwoProfilesSameInterface(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        //first & second profile links in array of profiles for MultipleProfileResource
        profiles.add("/a/b/c/multiple-profile-resource-a");
        profiles.add("/a/b/c/multiple-profile-resource-b");

        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);


        assertThat(
            "even with 2 profiles matching same interface, it should only be returned once",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(HyperResource.class, MultipleProfileResource.class)
        );

    }


    @Test
    public void testDetermineInterfacesProfileNotRegisteredWithStrategy(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("/a/b/c/not-in-registry-resource");

        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);


        assertThat(
            "if profile is not registered with strategy it should not be returned",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(HyperResource.class)
        );

    }


    @Test
    public void testDetermineInterfacesProfileNotRegisteredAsExpectedInterface(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();

        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(NotInRegistryResource.class, mockResource);


        assertThat(
            "",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(NotInRegistryResource.class)
        );

    }


    @Test
    public void testDetermineInterfacesProfileMatchesSameInterfaceAsExpected(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("/a/b/c/profile-resource-1");
        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(ProfileResource1.class, mockResource);


        assertThat(
            "if profile is same as expected it only comes back once",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(ProfileResource1.class)
        );

    }


    @Test
    public void testDetermineInterfacesProfileMatchesTwoDifferentInterfaces(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        //this profile matches two interfaces
        profiles.add("/a/b/c/profile-resource-3");
        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);


        assertThat(
            "if profile matches two interfaces, both should come back",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(HyperResource.class, ProfileResource3A.class, ProfileResource3B.class)
        );

    }


    @Test
    public void testDetermineInterfacesProfilesMatchDifferentInterfaces(){
        ProfileBasedInterfaceSelectionStrategy x = new ProfileBasedInterfaceSelectionStrategy(interfaces);

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("/a/b/c/profile-resource-1");
        profiles.add("/a/b/c/profile-resource-2");
        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles())
        .thenReturn(profiles);


        Class<?>[] result = x.determineInterfaces(HyperResource.class, mockResource);


        assertThat(
            "two matches from two profiles should both come back",
            result,
            Matchers.<Class<?>>arrayContainingInAnyOrder(HyperResource.class, ProfileResource1.class, ProfileResource2.class)
        );

    }
}
