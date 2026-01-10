import React from 'react';
import { Trophy } from 'lucide-react';
import { leaderboard } from '../../../data/mockData';

export default function Leaderboard() {
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700">Rank</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700">Username</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700">Score</th>
              <th className="px-6 py-4 text-left text-sm font-semibold text-gray-700">Problems Solved</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {leaderboard.map((user) => (
              <tr key={user.rank} className="hover:bg-gray-50 transition-colors">
                <td className="px-6 py-4">
                  <div className="flex items-center gap-2">
                    {user.rank <= 3 && (
                      <Trophy
                        size={20}
                        className={
                          user.rank === 1
                            ? 'text-yellow-500'
                            : user.rank === 2
                            ? 'text-gray-400'
                            : 'text-orange-600'
                        }
                      />
                    )}
                    <span className="font-semibold text-gray-800">{user.rank}</span>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <span className="font-medium text-blue-600">{user.username}</span>
                </td>
                <td className="px-6 py-4">
                  <span className="font-semibold text-gray-800">{user.score}</span>
                </td>
                <td className="px-6 py-4">
                  <span className="text-gray-600">{user.solved}</span>
                </td>
                
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}