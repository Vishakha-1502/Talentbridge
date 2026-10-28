'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function JobseekerIndex() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/jobseeker/dashboard');
  }, [router]);

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
    </div>
  );
}
