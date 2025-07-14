import { create } from "zustand";

interface AuthState {
  isLoggedIn: boolean | null;
  setIsLoggedIn: (loggedIn: boolean) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isLoggedIn: null,
  setIsLoggedIn: (loggedIn) => set({ isLoggedIn: loggedIn }),
}));
