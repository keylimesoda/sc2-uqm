/*
 * Windows crash handler for UQM - separate file to avoid
 * windows.h conflicts with UQM's own RECT/POINT/etc types.
 */

#ifdef WIN32

#include <windows.h>
#include <dbghelp.h>
#include <stdio.h>
#include <string.h>

static LONG WINAPI UQM_CrashFilter(EXCEPTION_POINTERS *ExceptionInfo)
{
	FILE *crashlog;
	EXCEPTION_RECORD *rec = ExceptionInfo->ExceptionRecord;
	CONTEXT *ctx = ExceptionInfo->ContextRecord;

	crashlog = fopen("crashlog.txt", "w");
	if (crashlog)
	{
		fprintf(crashlog, "=== UQM CRASH REPORT ===\n");
		fprintf(crashlog, "Exception Code: 0x%08lX\n",
				(unsigned long)rec->ExceptionCode);
		fprintf(crashlog, "Exception Address: 0x%p\n",
				rec->ExceptionAddress);
		if (rec->ExceptionCode == EXCEPTION_ACCESS_VIOLATION
				&& rec->NumberParameters >= 2)
		{
			fprintf(crashlog, "Access Type: %s\n",
					rec->ExceptionInformation[0] == 0 ? "READ" :
					rec->ExceptionInformation[0] == 1 ? "WRITE" : "EXECUTE");
			fprintf(crashlog, "Faulting Address: 0x%p\n",
					(void *)rec->ExceptionInformation[1]);
		}
#ifdef _WIN64
		fprintf(crashlog, "RIP: 0x%016llX\n", (unsigned long long)ctx->Rip);
		fprintf(crashlog, "RSP: 0x%016llX\n", (unsigned long long)ctx->Rsp);
		fprintf(crashlog, "RBP: 0x%016llX\n", (unsigned long long)ctx->Rbp);
		fprintf(crashlog, "RAX: 0x%016llX\n", (unsigned long long)ctx->Rax);
		fprintf(crashlog, "RBX: 0x%016llX\n", (unsigned long long)ctx->Rbx);
		fprintf(crashlog, "RCX: 0x%016llX\n", (unsigned long long)ctx->Rcx);
		fprintf(crashlog, "RDX: 0x%016llX\n", (unsigned long long)ctx->Rdx);
		fprintf(crashlog, "RSI: 0x%016llX\n", (unsigned long long)ctx->Rsi);
		fprintf(crashlog, "RDI: 0x%016llX\n", (unsigned long long)ctx->Rdi);
		fprintf(crashlog, "R8:  0x%016llX\n", (unsigned long long)ctx->R8);
		fprintf(crashlog, "R9:  0x%016llX\n", (unsigned long long)ctx->R9);
		fprintf(crashlog, "R10: 0x%016llX\n", (unsigned long long)ctx->R10);
		fprintf(crashlog, "R11: 0x%016llX\n", (unsigned long long)ctx->R11);
		fprintf(crashlog, "R12: 0x%016llX\n", (unsigned long long)ctx->R12);
		fprintf(crashlog, "R13: 0x%016llX\n", (unsigned long long)ctx->R13);
		fprintf(crashlog, "R14: 0x%016llX\n", (unsigned long long)ctx->R14);
		fprintf(crashlog, "R15: 0x%016llX\n", (unsigned long long)ctx->R15);
#else
		fprintf(crashlog, "EIP: 0x%08lX\n", (unsigned long)ctx->Eip);
		fprintf(crashlog, "ESP: 0x%08lX\n", (unsigned long)ctx->Esp);
		fprintf(crashlog, "EBP: 0x%08lX\n", (unsigned long)ctx->Ebp);
#endif
		
		/* Walk the stack */
		{
			HANDLE process = GetCurrentProcess();
			HANDLE thread = GetCurrentThread();
			STACKFRAME64 frame;
			DWORD machineType;
			int frameNum;
			
			SymInitialize(process, NULL, TRUE);
			memset(&frame, 0, sizeof(frame));
#ifdef _WIN64
			machineType = IMAGE_FILE_MACHINE_AMD64;
			frame.AddrPC.Offset = ctx->Rip;
			frame.AddrFrame.Offset = ctx->Rbp;
			frame.AddrStack.Offset = ctx->Rsp;
#else
			machineType = IMAGE_FILE_MACHINE_I386;
			frame.AddrPC.Offset = ctx->Eip;
			frame.AddrFrame.Offset = ctx->Ebp;
			frame.AddrStack.Offset = ctx->Esp;
#endif
			frame.AddrPC.Mode = AddrModeFlat;
			frame.AddrFrame.Mode = AddrModeFlat;
			frame.AddrStack.Mode = AddrModeFlat;
			
			fprintf(crashlog, "\n=== STACK TRACE ===\n");
			for (frameNum = 0; frameNum < 30; frameNum++)
			{
				char symbolBuffer[sizeof(SYMBOL_INFO) + 256];
				SYMBOL_INFO *symbol = (SYMBOL_INFO *)symbolBuffer;
				DWORD64 displacement = 0;
				
				if (!StackWalk64(machineType, process, thread, &frame,
						ctx, NULL, SymFunctionTableAccess64,
						SymGetModuleBase64, NULL))
					break;
				if (frame.AddrPC.Offset == 0)
					break;
				
				symbol->SizeOfStruct = sizeof(SYMBOL_INFO);
				symbol->MaxNameLen = 255;
				if (SymFromAddr(process, frame.AddrPC.Offset,
						&displacement, symbol))
				{
					fprintf(crashlog, "  #%d  0x%p  %s + 0x%llx\n",
							frameNum,
							(void *)(uintptr_t)frame.AddrPC.Offset,
							symbol->Name,
							(unsigned long long)displacement);
				}
				else
				{
					fprintf(crashlog, "  #%d  0x%p  (unknown)\n",
							frameNum,
							(void *)(uintptr_t)frame.AddrPC.Offset);
				}
			}
			SymCleanup(process);
		}
		
		fflush(crashlog);
		fclose(crashlog);
	}
	
	/* Also dump to stderr */
	fprintf(stderr, "\n=== UQM CRASH: Exception 0x%08lX at %p ===\n",
			(unsigned long)rec->ExceptionCode, rec->ExceptionAddress);
	if (rec->ExceptionCode == EXCEPTION_ACCESS_VIOLATION
			&& rec->NumberParameters >= 2)
	{
		fprintf(stderr, "Access Violation: %s address 0x%p\n",
				rec->ExceptionInformation[0] == 0 ? "reading" :
				rec->ExceptionInformation[0] == 1 ? "writing" : "executing",
				(void *)rec->ExceptionInformation[1]);
	}
	fflush(stderr);
	
	return EXCEPTION_CONTINUE_SEARCH;
}

void UQM_InstallCrashHandler(void)
{
	SetUnhandledExceptionFilter(UQM_CrashFilter);
}

#endif /* WIN32 */
