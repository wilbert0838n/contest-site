import React, { useState, useEffect } from 'react';
import { Trophy, Code, ChevronRight } from 'lucide-react';
import Navbar from './components/common/Navbar';
import AuthModal from './components/common/AuthModal';
import Leaderboard from './components/features/leaderboard/Leaderboard';
import ProblemWorkspace from './components/features/problems/ProblemWorkspace';
import HackingView from './components/features/hacking/HackingView';
import { API_BASE_URL } from './config';

const initialWeeks = [
  { id: 1, title: "Week 1 - Arrays & Strings", problems: [] }
];

export default function App() {
  // --- Global State ---
  const [activeTab, setActiveTab] = useState('problems');
  const [selectedWeek, setSelectedWeek] = useState(null);

  // --- Week Data ---
  const [weeksData, setWeeksData] = useState(initialWeeks);
  const [loadingProblems, setLoadingProblems] = useState(false);

  // --- Navigation State ---
  const [selectedProblem, setSelectedProblem] = useState(null);
  const [selectedSubmission, setSelectedSubmission] = useState(null);

  // --- Auth State ---
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState('');

  // --- Helper Functions for UI ---
  const getDifficultyColor = (difficulty) => {
    switch (difficulty) {
      case 'Easy': return 'text-green-600 bg-green-50';
      case 'Medium': return 'text-yellow-600 bg-yellow-50';
      case 'Hard': return 'text-red-600 bg-red-50';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  const getAccuracyColor = (accuracy) => {
    if (accuracy >= 70) return 'text-green-600';
    if (accuracy >= 50) return 'text-yellow-600';
    return 'text-red-600';
  };

  // --- Auth Effects ---
  useEffect(() => {
    fetch(`${API_BASE_URL}/api/me`, {
      credentials: 'include', // IMPORTANT: Sends the Session Cookie to backend
        redirect: 'manual',
    })
      .then(response => {
        if (response.ok) return response.json();
        throw new Error('Not logged in');
      })
      .then(data => {
        setIsLoggedIn(true);
        setUsername(data.name); // Using the name from Google
      })
      .catch(err => {
        setIsLoggedIn(false);
      });
  }, []);

  const handleWeekExpand = async (weekId) => {
    // A. Toggle logic (Close if already open)
    if (selectedWeek === weekId) {
      setSelectedWeek(null);
      return;
    }

    setSelectedWeek(weekId);

    // B. Check if we already fetched problems for this week (Optimization)
    const weekIndex = weeksData.findIndex(w => w.id === weekId);
    if (weeksData[weekIndex].problems.length > 0) {
      return; // Already have data, don't fetch again
    }

    // C. Fetch from Backend
    setLoadingProblems(true);
    try {
      // Calls your: @GetMapping(params = "contestId")
      const response = await fetch(`${API_BASE_URL}/api/problems?contestId=${weekId}`, {
        credentials: 'include'
      });
      if (!response.ok) throw new Error("Failed to fetch");

      const problemsFromBackend = await response.json();

      // D. Update State
      setWeeksData(prevWeeks => prevWeeks.map(week => {
        if (week.id === weekId) {
          return { ...week, problems: problemsFromBackend };
        }
        return week;
      }));

    } catch (error) {
      console.error("Error loading problems:", error);
    } finally {
      setLoadingProblems(false);
    }
  };

  const handleLogout = async () => {
    try {
      // 1. Tell Backend to kill the session
      await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST', // Spring Security logout requires POST by default
        credentials: 'include' // Send the cookie so it knows WHICH session to kill
      });
    } catch (error) {
      console.error("Logout failed", error);
    } finally {
      // 2. Clear Frontend State
      setIsLoggedIn(false);
      setUsername('');
      alert('Logged out successfully!');
    }
  };

  const handleOAuthLogin = (provider) => {
    // Redirects browser to Spring Boot's OAuth2 endpoint
    window.location.href = `${API_BASE_URL}/oauth2/authorization/${provider}`;
  };

  // --- Render Logic ---


  // 3. Dashboard View (Default)
  return (
    <div className="min-h-screen bg-gray-50">

      {/* 1. GLOBAL MODAL - Now lives outside the specific views */}
      {showAuthModal && (
        <AuthModal
          onClose={() => setShowAuthModal(false)}
          onGoogleLogin={() => handleOAuthLogin('google')}
          onGithubLogin={() => handleOAuthLogin('github')}
        />
      )}

      {/* 2. VIEW SWITCHER - Decides what main content to show */}
      {selectedSubmission ? (
        // VIEW A: Hacking
        <HackingView
          submission={selectedSubmission}
          onBack={() => setSelectedSubmission(null)}
        />
      ) : selectedProblem ? (
        // VIEW B: Problem Workspace
        <ProblemWorkspace
          problem={selectedProblem}
          onBack={() => setSelectedProblem(null)}
          onSelectSubmission={(sub) => setSelectedSubmission(sub)}
          isLoggedIn={isLoggedIn}
          setIsLoggedIn={setIsLoggedIn}
          onShowAuth={() => setShowAuthModal(true)}
        />
      ) : (
        // VIEW C: Dashboard (Default)
        <>
          <Navbar
            isLoggedIn={isLoggedIn}
            username={username}
            onLogout={handleLogout}
            onAuthClick={() => setShowAuthModal(true)}
          />

          <div className="max-w-7xl mx-auto p-6">
            {/* Tab Switcher */}
            <div className="bg-white rounded-lg shadow-md mb-4">
              <div className="flex border-b">
                <button
                  onClick={() => setActiveTab('problems')}
                  className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'problems'
                      ? 'text-blue-600 border-b-2 border-blue-600'
                      : 'text-gray-600 hover:text-gray-800'
                    }`}
                >
                  <Code size={20} /> Problems
                </button>
                <button
                  onClick={() => setActiveTab('leaderboard')}
                  className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'leaderboard'
                      ? 'text-blue-600 border-b-2 border-blue-600'
                      : 'text-gray-600 hover:text-gray-800'
                    }`}
                >
                  <Trophy size={20} /> Leaderboard
                </button>
              </div>
            </div>

            {/* Tab Content */}
            {activeTab === 'problems' && (
              <div className="space-y-6">
                {weeksData.map((week) => (
                  <div key={week.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                    {/* ... Your existing Week/Problem mapping code ... */}
                    <div
                      onClick={() => handleWeekExpand(week.id)}
                      className="bg-linear-to-r from-blue-500 to-blue-600 text-white p-4 cursor-pointer hover:from-blue-600 hover:to-blue-700 transition-all flex justify-between items-center"
                    >
                      <h2 className="text-xl font-bold">{week.title}</h2>
                      <ChevronRight
                        size={24}
                        className={`transform transition-transform ${selectedWeek === week.id ? 'rotate-90' : ''
                          }`}
                      />
                    </div>

                    {/* COPY PASTE YOUR EXISTING PROBLEMS LIST CODE HERE */}
                    {selectedWeek === week.id && (
                      <div className="divide-y">
                        {/* ... map through week.problems ... */}
                        {week.problems.map((problem) => (
                          <div
                            key={problem.id}
                            onClick={() => setSelectedProblem(problem)}
                            className="p-4 hover:bg-gray-50 cursor-pointer transition-colors flex items-center justify-between"
                          >
                            <div className="flex items-center gap-4 flex-1">
                              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                                <span className="text-blue-600 font-bold text-lg">{problem.id}</span>
                              </div>

                              <div className="flex-1">
                                <h3 className="font-semibold text-gray-800 mb-1">{problem.problemName}</h3>

                                {/* --- RESTORED STATS SECTION --- */}
                                <div className="flex items-center gap-3 text-sm text-gray-600">
                                  <span className={`px-2 py-1 rounded text-xs font-medium ${getDifficultyColor(problem.difficulty)}`}>
                                    {problem.difficulty}
                                  </span>

                                  {/* Solved Count */}
                                  <span>✓ {problem.solved || 0} solved</span>

                                  {/* Accuracy */}
                                  <span className={`font-semibold ${getAccuracyColor(problem.accuracy)}`}>
                                    {problem.accuracy || 0}% accuracy
                                  </span>
                                </div>
                                {/* ----------------------------- */}

                              </div>
                            </div>
                            <ChevronRight size={20} className="text-gray-400" />
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {activeTab === 'leaderboard' && <Leaderboard />}
          </div>
        </>
      )}
    </div>
  );
}