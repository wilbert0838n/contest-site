import React, { useState, useEffect } from 'react';
import { Zap, AlertCircle } from 'lucide-react';

export default function HackingView({ submission, onBack }) {
  const [hackTest, setHackTest] = useState('');

  // 1. New State for the full data (code, etc.)
  const [fullSubmission, setFullSubmission] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 2. Fetch the Code when component mounts
  useEffect(() => {
    // If we didn't get an ID, stop.
    if (!submission?.id) return;

    setLoading(true);
    fetch(`/api/submissions/user/${submission.id}`, {
      credentials: 'include' // Important for auth
    })
      .then(res => {
        if (!res.ok) throw new Error("Could not fetch code");
        return res.json();
      })
      .then(data => {
        setFullSubmission(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setError("Failed to load submission code.");
        setLoading(false);
      });
  }, [submission]);

  const handleHack = () => {
    // API Call for hacking would go here
    alert(`Hacking attempt submitted for Submission #${submission.id}`);
    onBack();
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-600 text-white p-4 shadow-md">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold">CodeArena - Hacking Phase</h1>
            <p className="text-blue-100 text-sm">Try to break this solution with a test case</p>
          </div>
          <button
            onClick={onBack}
            className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition-colors"
          >
            ← Back to Submissions
          </button>
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

          {/* Left Column: Submission Details & Code */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="mb-4 pb-4 border-b">
              <h2 className="text-xl font-bold text-gray-800 mb-2">Submission #{submission.id}</h2>
              <div className="flex gap-4 text-sm text-gray-600">
                <span className="font-semibold text-blue-600">{submission.username}</span>
                <span>Language: {submission.language}</span>
                {/* Use optional chaining because fullSubmission might be loading */}
                <span>Time: {fullSubmission?.executionTime || submission.time}</span>
              </div>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Submitted Code</h3>

              {/* 3. Render Logic based on State */}
              {loading ? (
                <div className="h-64 bg-gray-100 rounded flex items-center justify-center text-gray-500 animate-pulse">
                  Loading code...
                </div>
              ) : error ? (
                <div className="h-64 bg-red-50 rounded flex items-center justify-center text-red-500 gap-2">
                  <AlertCircle size={20} /> {error}
                </div>
              ) : (
                <pre className="bg-gray-900 text-gray-100 p-4 rounded text-sm font-mono overflow-x-auto" style={{ maxHeight: '60vh' }}>
                  {/* 4. SHOW THE REAL CODE */}
                  {fullSubmission.code}
                </pre>
              )}
            </div>
          </div>

          {/* Right Column: Hacking Interface (Unchanged) */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-gray-800 mb-2 flex items-center gap-2">
                <Zap className="text-orange-500" size={20} />
                Create Hacking Test Case
              </h3>
              <p className="text-sm text-gray-600 mb-4">
                Create a test case that you think will make this solution fail.
              </p>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Your Hacking Input
              </label>
              <textarea
                value={hackTest}
                onChange={(e) => setHackTest(e.target.value)}
                placeholder="Enter your test case input here..."
                className="w-full h-48 p-3 border border-gray-300 rounded font-mono text-sm resize-none focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
            </div>

            <button
              onClick={handleHack}
              disabled={!hackTest.trim()}
              className="w-full bg-orange-600 hover:bg-orange-700 disabled:bg-gray-400 text-white font-semibold py-3 px-6 rounded flex items-center justify-center gap-2 transition-colors"
            >
              <Zap size={18} />
              Submit Hack
            </button>
            <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded">
              <h4 className="font-semibold text-blue-900 mb-2">Hacking Rules:</h4>
              <ul className="text-sm text-blue-800 space-y-1">
                <li>• Your test case must follow the problem's input format</li>
                <li>• If the solution fails on your test, you earn +100 points</li>
                <li>• If the solution passes, you lose -50 points</li>
                <li>• Each submission can only be hacked once per user</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}