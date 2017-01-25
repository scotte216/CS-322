	.text
			  # _go (i, j, k) 
	.p2align 4,0x90
	.globl _go
_go:
	subq $40,%rsp
	movl %edi,(%rsp)
	movl %esi,4(%rsp)
	movl %edx,8(%rsp)
			  #  t1 = i + j
	movslq (%rsp),%r10
	movslq 4(%rsp),%r11
	addq %r11,%r10
	movl %r10d,12(%rsp)
			  #  t2 = t1 + k
	movslq 12(%rsp),%r10
	movslq 8(%rsp),%r11
	addq %r11,%r10
	movl %r10d,16(%rsp)
			  #  return t2
	movslq 16(%rsp),%rax
	addq  $40,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
	subq $4,%rsp
			  #  t3 = call _go(1, 2, 3)
	movq $1,%rdi
	movq $2,%rsi
	movq $3,%rdx
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t3)
	movslq (%rsp),%rdi
	call _printInt
			  #  return 
	addq  $4,%rsp
	ret
			  # Total inst cnt: 30
