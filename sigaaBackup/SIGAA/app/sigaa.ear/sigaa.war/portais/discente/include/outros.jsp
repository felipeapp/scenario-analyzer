    <ul>
		<li><h:commandLink value="Avisar Aus�ncia do Professor" action="#{ avisoFalta.iniciar }" /> </li>
	</ul>
	<ul>
		<li> Coordena��o de Curso
			<ul>
				<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
					<li><h:commandLink value="Atendimento ao Aluno" action="#{ atendimentoAluno.novaPergunta }"/> </li> 
				</c:if>
				<li><h:commandLink value="F�rum de Cursos" action="#{ forum.listarForunsCurso }" /> </li>
				<li><a href="${ctx}/public/curso/portal.jsf?id=${usuario.discenteAtivo.curso.id }&lc=pt_BR" target="">P�gina do Curso</a></li>
			</ul>
		</li>
	</ul>
	<c:if test="${usuario.discenteAtivo.graduacao}">
		<ul>	
			<li> Requerimento Padr�o
				<ul>
					<li><h:commandLink value="Criar Requerimento Padr�o" action="#{ requerimento.requerimentoPadrao }" /> </li>
					<li><h:commandLink value="Exibir Meus Requerimentos" action="#{ requerimento.exibirRequerimento }" /> </li>
				</ul>
			</li>
	   	</ul>
	</c:if>
	<ul>
		<li> Produ��es Intelectuais
			<ul>
				<li><h:commandLink value="Acervo dos Docentes" action="#{acervoProducao.verAcervoDigital}" /> </li>
				<li><h:commandLink value="Consultar Defesas de P�s-gradua��o" action="#{consultarDefesaMBean.iniciar}" /> </li>
			</ul>
		</li>
	</ul>	
	<ul>
		<li> Fiscal do Vestibular
			<ul>
				<li><h:commandLink value="Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"/> </li>
				<li><h:commandLink value="Resultado da Sele��o" action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}"/> </li>
				<li><h:commandLink value="Lista de Locais de Aplica��o de Prova" action="#{relatoriosVestibular.iniciarListaLocaisProva}"/> </li>
				<li><h:commandLink value="Comprovante de Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"/> </li>
				<li><h:commandLink value="Justificativa de Aus�ncia" action="#{justificativaAusencia.preCadastrar}"/> </li>
			</ul>
		</li>
	</ul>	
	<ul>			
		<li><h:commandLink value="Criar senha de acesso por Celular" action="#{ senhaMobileMBean.iniciar }" />
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
			<li><h:commandLink value="Consultar Processos" action="#{ consultaProcesso.iniciar }" /> </li>
		</c:if>
    </ul>