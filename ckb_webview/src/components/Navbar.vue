<script setup lang="ts">
import { store } from '@/store';
import { RouterLink } from 'vue-router';
import NavItem from './NavItem.vue';
import ProfileFrame from './ProfileFrame.vue';
import IconNotification from './icons/IconNotification.vue'

const logged_in: boolean = store.getters.isLoggedIn; 
const user_data = store.getters.getUserData; 
</script> 

<template>
    <div class="nav-wrapper">
        <img alt="CKB Logo" class="ckb-logo" src="@/assets/generic_logo.svg" width="50" height="50" />
        <nav>
            <ul>
                <li>
                    <NavItem url="/" highlight="false">Product</NavItem>
                </li>
                <li>
                    <NavItem url="/" highlight="false">About</NavItem>
                </li>
                <li v-if="!logged_in">
                    <NavItem url="/login" :highlight="true">Login</NavItem>
                </li>
                <li v-if="logged_in" >
                    <RouterLink to="/dashboard" >  
                        <IconNotification class="nav-image-wrapper" />
                    </RouterLink>
                </li>
                <li v-if="logged_in" >
                    <RouterLink to="/dashboard" >
                        <ProfileFrame class="nav-image-wrapper" :image_url="user_data.profile_img_url" :alt="user_data.username" />
                    </RouterLink>
                </li>
            </ul>
        </nav>
   </div>
</template>

<style scoped>
.nav-wrapper {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

nav { 
    font-size: 16px;
    text-align: center;
} 

nav ul {
    list-style-type: none;
    display: flex;
}

nav ul li {
    margin: 1rem 2rem 1rem 2rem;
    justify-content: center;
    align-content: center;
}

nav ul li:hover {
    color: hsla(255, 100%, 100%, 1);
}

.nav-image-wrapper {
    width: 30px;
    height: 30px;
}
</style>
