    <ul>
		<li><h:commandLink value="Avisar Ausência do Professor" action="#{ avisoFalta.iniciar }" /> </li>
	</ul>
	<ul>
		<li> Coordenação de Curso
			<ul>
				<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
					<li><h:commandLink value="Atendimento ao Aluno" action="#{ atendimentoAluno.novaPergunta }"/> </li> 
				</c:if>
				<li><h:commandLink value="Fórum de Cursos" action="#{ forum.listarForunsCurso }" /> </li>
				<li><a href="${ctx}/public/curso/portal.jsf?id=${usuario.discenteAtivo.curso.id }&lc=pt_BR" target="">Página do Curso</a></li>
			</ul>
		</li>
	</ul>
	<c:if test="${usuario.discenteAtivo.graduacao}">
		<ul>	
			<li> Requerimento Padrão
				<ul>
					<li><h:commandLink value="Criar Requerimento Padrão" action="#{ requerimento.requerimentoPadrao }" /> </li>
					<li><h:commandLink value="Exibir Meus Requerimentos" action="#{ requerimento.exibirRequerimento }" /> </li>
				</ul>
			</li>
	   	</ul>
	</c:if>
	<ul>
		<li> Produções Intelectuais
			<ul>
				<li><h:commandLink value="Acervo dos Docentes" action="#{acervoProducao.verAcervoDigital}" /> </li>
				<li><h:commandLink value="Consultar Defesas de Pós-graduação" action="#{consultarDefesaMBean.iniciar}" /> </li>
			</ul>
		</li>
	</ul>	
	<ul>
		<li> Fiscal do Vestibular
			<ul>
				<li><h:commandLink value="Inscrição" action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"/> </li>
				<li><h:commandLink value="Resultado da Seleção" action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}"/> </li>
				<li><h:commandLink value="Lista de Locais de Aplicação de Prova" action="#{relatoriosVestibular.iniciarListaLocaisProva}"/> </li>
				<li><h:commandLink value="Comprovante de Inscrição" action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"/> </li>
				<li><h:commandLink value="Justificativa de Ausência" action="#{justificativaAusencia.preCadastrar}"/> </li>
			</ul>
		</li>
	</ul>	
	<ul>			
		<li><h:commandLink value="Criar senha de acesso por Celular" action="#{ senhaMobileMBean.iniciar }" />
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
			<li><h:commandLink value="Consultar Processos" action="#{ consultaProcesso.iniciar }" /> </li>
		</c:if>
    </ul>