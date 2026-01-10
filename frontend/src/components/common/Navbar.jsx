import React from 'react';
import { User, LogOut, LogIn } from 'lucide-react';

export default function Navbar({ isLoggedIn, username, onLogout, onAuthClick }) {
  return (
    <div className="bg-blue-600 text-white p-4 shadow-md">
      <div className="max-w-7xl mx-auto flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Contesting</h1>
          <p className="text-blue-100 text-sm">Competitive Programming Platform</p>
        </div>
        <div className="flex items-center gap-4">
          {isLoggedIn ? (
            <>
              <div className="flex items-center gap-2 bg-blue-700 px-4 py-2 rounded-lg">
                <User size={20} />
                <span className="font-medium">{username}</span>
              </div>
              <button
                onClick={onLogout}
                className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
              >
                <LogOut size={18} />
                Logout
              </button>
            </>
          ) : (
            <button
              onClick={onAuthClick}
              className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
            >
              <LogIn size={18} />
              Login
            </button>
          )}
        </div>
      </div>
    </div>
  );
}