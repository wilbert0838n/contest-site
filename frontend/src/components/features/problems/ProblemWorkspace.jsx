import React, { useState, useEffect } from 'react';
import { Play, Check, X, Users, Eye } from 'lucide-react';
import { problemData, successfulSubmissions } from '../../../data/mockData';
import {API_BASE_URL} from '../../../config';

export default function ProblemWorkspace({ problem, onBack, onSelectSubmission, isLoggedIn, setIsLoggedIn, onShowAuth }) {
    const [activeTab, setActiveTab] = useState('problem');
    const [code, setCode] = useState('');
    const [language, setLanguage] = useState('CPP');
    const [isRunning, setIsRunning] = useState(false);
    const [verdict, setVerdict] = useState(''); // <--- 1. NEW STATE
    const [testResults, setTestResults] = useState([]);
    const [submissionList,setSubmissionsList] = useState([]);

    // Inside ProblemWorkspace.jsx

    useEffect(() => {
        if (activeTab === 'submissions' && isLoggedIn) {
            // FETCH ONLY SUCCESSFUL ONES
            fetch(`${API_BASE_URL}/api/submissions/problem/${problem.id}/successful`, {
                credentials: 'include'
            })
                .then(res => res.json())
                .then(data => setSubmissionsList(data));
        }
    }, [activeTab, problem.id]);

    const handleRun = async () => {
        if (!isLoggedIn) {
            onShowAuth(); // Open the login modal
            return;       // Stop the function here
        }

        setIsRunning(true);
        setVerdict('Running...'); // Reset verdict while running
        setTestResults([]);

        const payload = {
            userId: 2,
            problemId: 1,
            language: language,
            code: code
        };

        try {
            const response = await fetch(`${API_BASE_URL}/api/run`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(payload),
            });

            if (!response.ok) throw new Error("Server Error");

            const result = await response.text();

            setIsRunning(false);
            // 2. UPDATE STATE WITH REAL DATA
            setVerdict(result);

            const isSuccess = result === "Accepted" || result === "All Testcase Passed";

            setTestResults([{
                id: "System Test",
                passed: isSuccess,
                time: '0.1s',
                memory: 'N/A'
            }]);

        } catch (error) {
            console.error("FULL ERROR DETAILS:", error);

            setIsRunning(false);
            setVerdict("System Error"); // Set verdict on error
            setTestResults([{
                id: "Error",
                passed: false,
                time: '-',
                memory: '-'
            }]);
        }
    };

    const getAccuracyColor = (accuracy) => {
        if (accuracy >= 70) return 'text-green-600';
        if (accuracy >= 50) return 'text-yellow-600';
        return 'text-red-600';
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Workspace Header */}
            <div className="bg-blue-600 text-white p-4 shadow-md">
                <div className="max-w-7xl mx-auto flex justify-between items-center">
                    <div>
                        <h1 className="text-2xl font-bold">CodeArena</h1>
                        <p className="text-blue-100 text-sm">Competitive Programming Platform</p>
                    </div>
                    <button
                        onClick={onBack}
                        className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded transition-colors"
                    >
                        ← Back to Problems
                    </button>
                </div>
            </div>

            <div className="max-w-7xl mx-auto p-6">
                {/* Tab Navigation */}
                <div className="bg-white rounded-lg shadow-md mb-4">
                    <div className="flex border-b">
                        <button
                            onClick={() => setActiveTab('problem')}
                            className={`px-6 py-3 font-semibold transition-colors ${activeTab === 'problem'
                                ? 'text-blue-600 border-b-2 border-blue-600'
                                : 'text-gray-600 hover:text-gray-800'
                                }`}
                        >
                            Problem
                        </button>
                        <button
                            onClick={() => setActiveTab('submissions')}
                            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'submissions'
                                ? 'text-blue-600 border-b-2 border-blue-600'
                                : 'text-gray-600 hover:text-gray-800'
                                }`}
                        >
                            <Users size={18} />
                            Submissions ({submissionList.length})
                        </button>
                    </div>
                </div>

                {/* View 1: Problem Description & Editor */}
                {activeTab === 'problem' && (
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {/* Left Column: Problem Details */}
                        <div className="bg-white rounded-lg shadow-md p-6 overflow-auto" style={{ maxHeight: '85vh' }}>
                            <div className="mb-4">
                                <h2 className="text-2xl font-bold text-gray-800 mb-2">{problem.problemName || problemData.title}</h2>
                                <div className="flex gap-4 text-sm text-gray-600 mb-2">
                                    <span>Time limit: {problemData.timeLimit}</span>
                                    <span>Memory limit: {problemData.memoryLimit}</span>
                                </div>
                                <div className="flex items-center gap-2 text-sm">
                                    <span className={`font-semibold ${getAccuracyColor(problem?.accuracy || 0)}`}>
                                        Accuracy: {problem?.accuracy || 0}%
                                    </span>
                                    <span className="text-gray-500">
                                        ({problem?.solved || 0} / {problem?.total || 0} submissions)
                                    </span>
                                </div>
                            </div>

                            <div className="space-y-6">
                                <div>
                                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Problem Statement</h3>
                                    <p className="text-gray-700 leading-relaxed">{problem.description}</p>
                                </div>

                                <div>
                                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Input Format</h3>
                                    <p className="text-gray-700 leading-relaxed whitespace-pre-line">{problem.inputFormat}</p>
                                </div>

                                <div>
                                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Output Format</h3>
                                    <p className="text-gray-700 leading-relaxed">{problem.outputFormat}</p>
                                </div>

                                <div>
                                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Examples</h3>
                                    {problem.examples.map((example, idx) => (
                                        <div key={idx} className="mb-4">
                                            <div className="grid grid-cols-2 gap-4">
                                                <div>
                                                    <p className="text-sm font-medium text-gray-600 mb-1">Input</p>
                                                    <pre className="bg-gray-100 p-3 rounded text-sm font-mono whitespace-pre overflow-x-auto">
                                                        {example.input}
                                                    </pre>
                                                </div>
                                                <div>
                                                    <p className="text-sm font-medium text-gray-600 mb-1">Output</p>
                                                    <pre className="bg-gray-100 p-3 rounded text-sm font-mono whitespace-pre overflow-x-auto">
                                                        {example.output}
                                                    </pre>
                                                </div>
                                                <div>
                                                    <p className="text-sm font-medium text-gray-600 mb-1">Explanation</p>
                                                    <pre className=" p-3 rounded text-sm font-mono whitespace-pre overflow-x-auto">
                                                        {example.explanation}
                                                    </pre>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>

                        {/* Right Column: Code Editor */}
                        <div className="bg-white rounded-lg shadow-md p-6 flex flex-col overflow-hidden" style={{ maxHeight: '85vh' }}>
                            <div className="mb-4">
                                <div className="flex justify-between items-center mb-2">
                                    <h3 className="text-lg font-semibold text-gray-800">Code Editor</h3>
                                    <select
                                        value={language}
                                        onChange={(e) => setLanguage(e.target.value)}
                                        className="border border-gray-300 rounded px-3 py-1 text-sm focus:outline-none focus:border-blue-500"
                                    >
                                        <option value="CPP">C++</option>
                                        <option value="PYTHON">Python</option>
                                        <option value="JAVA">Java</option>
                                    </select>
                                </div>
                            </div>

                            <textarea
                                value={code}
                                onChange={(e) => setCode(e.target.value)}
                                placeholder="// Write your code here..."
                                className="w-full p-4 border border-gray-300 rounded font-mono text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                                style={{ height: '300px' }}
                            />

                            <button
                                onClick={handleRun}
                                disabled={isRunning}
                                className="mt-4 bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white font-semibold py-2 px-6 rounded flex items-center justify-center gap-2 transition-colors shrink-0"
                            >
                                <Play size={18} />
                                {isRunning ? 'Running...' : 'Run Code'}
                            </button>

                            {/* --- 3. UPDATED RESULTS SECTION --- */}
                            {testResults.length > 0 && (
                                <div className="mt-4 border-t pt-4 overflow-y-auto flex-1">
                                    <div className="flex items-center gap-2 mb-3">
                                        {/* Dynamic Icon based on verdict */}
                                        {verdict === "Accepted" || verdict === "All Testcase Passed" ? (
                                            <Check size={20} className="text-green-600" />
                                        ) : (
                                            <X size={20} className="text-red-600" />
                                        )}

                                        {/* Dynamic Text and Color */}
                                        <h4 className={`font-bold text-lg ${verdict === "Accepted" || verdict === "All Testcase Passed"
                                            ? "text-green-600"
                                            : "text-red-600"
                                            }`}>
                                            {verdict}
                                        </h4>
                                    </div>

                                    <div className="bg-gray-50 rounded-lg p-3 space-y-2">
                                        {testResults.map((result) => (
                                            <div key={result.id} className="flex items-center justify-between">
                                                <div className="flex items-center gap-2">
                                                    {result.passed ? (
                                                        <div className="w-5 h-5 rounded-full bg-green-500 flex items-center justify-center">
                                                            <Check size={14} className="text-white" />
                                                        </div>
                                                    ) : (
                                                        <div className="w-5 h-5 rounded-full bg-red-500 flex items-center justify-center">
                                                            <X size={14} className="text-white" />
                                                        </div>
                                                    )}
                                                    <span className="text-sm text-gray-700">Test case {result.id}</span>
                                                </div>
                                                <div className="text-xs text-gray-500">
                                                    {result.time} • {result.memory}
                                                </div>
                                            </div>
                                        ))}
                                    </div>


                                </div>
                            )}
                        </div>
                    </div>
                )}

                {/* View 2: Submissions List */}
                {activeTab === 'submissions' && (
                    <div className="bg-white rounded-lg shadow-md overflow-hidden">
                        <div className="p-6 bg-linear-to-r from-green-50 to-blue-50 border-b">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">Successful Submissions</h3>
                            <p className="text-sm text-gray-600">
                                Click on any submission to view the code and attempt to hack it. Successful hacks earn you points!
                            </p>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead className="bg-gray-50 border-b">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Username</th>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Language</th>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Time</th>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Memory</th>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">When</th>
                                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Action</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y">
                                    {submissionList.map((submission) => (
                                        <tr key={submission.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4">
                                                <span className="font-medium text-blue-600">{submission.username}</span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className="text-gray-700">{submission.language}</span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className="text-gray-700">{submission.executionTime}</span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className="text-gray-700">{submission.memoryUsed}</span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className="text-gray-600 text-sm">{submission.submittedAt}</span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <button
                                                    onClick={() => onSelectSubmission(submission)}
                                                    className="flex items-center gap-1 text-orange-600 hover:text-orange-700 font-medium text-sm transition-colors"
                                                >
                                                    <Eye size={16} />
                                                    View & Hack
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}