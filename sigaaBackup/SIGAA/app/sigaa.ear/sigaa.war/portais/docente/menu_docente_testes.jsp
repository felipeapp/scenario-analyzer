<%-- MENU DE OP��ES PARA O DOCENTE --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<div id="menu-dropdown">
<div class="wrapper">
<h:form>
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	<input type="hidden" name="jscook_action"/>
	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		<t:navigationMenuItem itemLabel="Cursos">
			<t:navigationMenuItem itemLabel="Consultar Cursos" actionListener="#{menuDocente.redirecionar}" itemValue="/geral/curso/busca_geral.jsf"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Disciplinas" rendered="#{acesso.nivelDocente == 'T'}">
			<t:navigationMenuItem itemLabel="Consultar Disciplinas" actionListener="#{menuDocente.consultaDisciplinaTec}"/>
			<t:navigationMenuItem itemLabel="Estrutura Curricular" actionListener="#{menuDocente.consultaEstruturaTec}"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Componentes Curriculares" rendered="#{acesso.nivelDocente == 'G'}">
			<t:navigationMenuItem itemLabel="Cadastrar Comp. Curriculares" actionListener="#{menuDocente.consultaCurriculo}" rendered="#{acesso.chefeDepartamento}"/>
			<t:navigationMenuItem itemLabel="Consultar Disciplinas" actionListener="#{menuDocente.consultaDisciplina}"/>
			<t:navigationMenuItem itemLabel="Consultar Estrutura Curricular" actionListener="#{menuDocente.redirecionar}" itemValue="/geral/estrutura_curricular/busca_geral.jsf"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Turmas">
			<t:navigationMenuItem itemLabel="Consultar Turma" actionListener="#{menuDocente.consultaTurma}"/>
			<t:navigationMenuItem itemLabel="Consolidar" action="consolidaTurma"/>
			<t:navigationMenuItem itemLabel="Criar Turma" rendered="#{acesso.chefeDepartamento}" action="#{turmaGradraduacao.preCadastrar}"/>
			<t:navigationMenuItem itemLabel="Alterar/Remover Turma" rendered="#{acesso.chefeDepartamento}" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/turma/lista.jsf"/>
	 		<t:navigationMenuItem itemLabel="Gerenciar Solicita��es de Turmas" rendered="#{acesso.chefeDepartamento}" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoes}"/>
	 		<t:navigationMenuItem itemLabel="Relat�rio de Turmas" rendered="#{acesso.chefeDepartamento}" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/turmas.jsf"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Discentes">
			<t:navigationMenuItem itemLabel="Buscar" action="consolidaTurma"/>
			<t:navigationMenuItem itemLabel="Matricular Aluno Especial" action="consolidaTurma" rendered="#{acesso.chefeDepartamento}"/>
			<t:navigationMenuItem itemLabel="Transferir Aluno" action="consolidaTurma" rendered="#{acesso.chefeDepartamento}"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Propostas">
			<t:navigationMenuItem itemLabel="Curso Lato Sensu" actionListener="#{menuDocente.redirecionar}" itemValue="/ensino/latosensu/criarCurso.do?dispatch=popular"/>
			<t:navigationMenuItem itemLabel="Projeto de Extens�o" actionListener="#{menuDocente.redirecionar}" itemValue="/extensao/Atividade/form.jsf"/>
			<t:navigationMenuItem itemLabel="Projeto de Monitoria" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/ProjetoMonitoria/form.jsf"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Monitoria" id="monitoria"  icon="/img/icones/monitoria_menu.gif">
		<t:navigationMenuItem itemLabel="Projetos">
			<t:navigationMenuItem itemLabel="Cadastrar Proposta de Projeto" action="#{projetoMonitoria.novoProjeto}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Monitores">
			<t:navigationMenuItem itemLabel="Consultar Lista de Monitores" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/ConsultarMonitor/monitores_usuario_logado.jsf"/>
			<t:navigationMenuItem itemLabel="Avaliar Relat�rios de Monitores" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/AtividadeMonitor/lista_avaliar_atividade.jsf"/>
			<t:navigationMenuItem itemLabel="Alterar Monitor" actionListener="#{menuDocente.redirecionar}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Editais" itemValue="/monitoria/EditalMonitoria/lista.jsf" actionListener="#{menuDocente.redirecionar}"/>

	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Pesquisa" id="pesquisa" icon="/img/icones/pesquisa_menu.gif">
		<t:navigationMenuItem itemLabel="�reas de conhecimento" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/cadastroAreaConhecimento.do?dispatch=list&page=0"/>
		<t:navigationMenuItem itemLabel="Projetos">
			<t:navigationMenuItem itemLabel="Submeter proposta/Cadastrar">
				<t:navigationMenuItem itemLabel="Projeto Interno" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular"/>
				<t:navigationMenuItem itemLabel="Projeto Externo" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Solicitar renova��o" split="true" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=listByCoordenador" actionListener="#{menuDocente.redirecionar}"/>
			<t:navigationMenuItem itemLabel="Listar meus projetos" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=listByMembro" actionListener="#{menuDocente.redirecionar}"/>

		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Planos de Trabalho">
			<t:navigationMenuItem itemLabel="Solicitar cotas" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=popular" actionListener="#{menuDocente.redirecionar}"/>
			<t:navigationMenuItem itemLabel="Listar/Alterar" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=listar&page=0" actionListener="#{menuDocente.redirecionar}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Relat�rios de Bolsa">
			<t:navigationMenuItem itemLabel="Relat�rios Parciais">
				<t:navigationMenuItem itemLabel="Emitir Parecer" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarOrientandos"/>
				<t:navigationMenuItem itemLabel="Consultar"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Relat�rios Finais">
				<t:navigationMenuItem itemLabel="Emitir Parecer"/>
				<t:navigationMenuItem itemLabel="Consultar"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Relat�rio Final">
			<t:navigationMenuItem itemLabel="Submeter" itemValue="/pesquisa/cadastroRelatorioProjeto.do?dispatch=edit" actionListener="#{menuDocente.redirecionar}"/>
			<t:navigationMenuItem itemLabel="Listar/Alterar" itemValue="/pesquisa/cadastroRelatorioProjeto.do?dispatch=list" actionListener="#{menuDocente.redirecionar}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Bolsas">
			<t:navigationMenuItem itemLabel="Indicar/Alterar Bolsista" itemValue="/pesquisa/indicarBolsista.do?dispatch=popular" actionListener="#{menuDocente.redirecionar}"/>
		</t:navigationMenuItem>
<%--
		<t:navigationMenuItem itemLabel="Editais"/>
--%>
	</t:navigationMenuItem>

	<t:navigationMenuItem id="producao_intelectual" itemLabel="Produ��o Intelectual" icon="/img/icones/producao_menu.gif">

		<t:navigationMenuItem itemLabel="Produ��es" id="producao">
			<t:navigationMenuItem itemLabel="Publica��es" id="publicacao">
					<t:navigationMenuItem itemLabel="Artigos, Peri�dicos, Jornais e Similares" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/Artigo/lista.jsf" id="artigo"/>
					<t:navigationMenuItem itemLabel="Cap�tulos de Livros" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/Capitulo/lista.jsf" id="capitulos"/>
					<t:navigationMenuItem itemLabel="Livros" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/Livro/lista.jsf" id="livros" />
					<t:navigationMenuItem itemLabel="Publica��es em Eventos"  actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/PublicacaoEvento/lista.jsf" id="publicacaoeventos" />
					<t:navigationMenuItem itemLabel="Textos Did�ticos" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/TextoDidatico/lista.jsf" id="textodidatico" />
					<t:navigationMenuItem itemLabel="Textos para Discuss�o" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/TextoDiscussao/lista.jsf" id="textodiscussao" />

			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Art�sticas, Liter�rias e Visual">
				<t:navigationMenuItem itemLabel="Audiovisuais" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/AudioVisual/lista.jsf" id="audiovisuais" />
				<t:navigationMenuItem itemLabel="Exposi��o ou Apresenta��es Art�sticas*"/>
				<t:navigationMenuItem itemLabel="Montagens*"/>
				<t:navigationMenuItem itemLabel="Programa��o Visual*" />
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Bancas Examinadoras/Sele��o para Curso">
				<t:navigationMenuItem itemLabel="Banca de Curso/Concurso" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/Banca/lista.jsf" id="banca" />
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Tecnol�gicas">
				<t:navigationMenuItem itemLabel="Maquetes, Prototipos e Outros" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/MaquetePrototipoOutro/lista.jsf" id="maqueteprototipo" />
				<t:navigationMenuItem itemLabel="Patente" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/Patente/lista.jsf" id="patente" />
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Outras Atividades">
				<t:navigationMenuItem itemLabel="Apresenta��o em Eventos" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/ApresentacaoEmEvento/lista.jsf" id="apresentacaoemevento"/>
				<t:navigationMenuItem itemLabel="Pr�mio Recebido" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/PremioRecebido/lista.jsf" id="premiorecebido"/>
				<t:navigationMenuItem itemLabel="Bolsas Obtidas*"/>
				<t:navigationMenuItem itemLabel="Visitas Cient�ficas" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/VisitaCientifica/lista.jsf" id="visitacientifica"/>
				<t:navigationMenuItem itemLabel="Participa��o em Comiss�o de Organiza��o de Eventos" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/ParticipacaoComissaoOrgEventos/lista.jsf" id="participacaocomissao"/>
				<t:navigationMenuItem itemLabel="Participa��o em Sociedades Cient�ficas e Culturais" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/ParticipacaoSociedade/lista.jsf" id="participacaosociedade"/>
				<t:navigationMenuItem itemLabel="Participa��o em Colegiados e Comiss�es" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/ParticipacaoColegiadoComissao/lista.jsf" id="participacaocolegiadocomissao"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Lista Produ��es" action="validacao"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Relat�rios">
			<t:navigationMenuItem itemLabel="Quantitativos de Produ��o Acad�mica" action="qtdproducoes"/>
			<t:navigationMenuItem itemLabel="Relat�rio da GED" actionListener="#{menuDocente.redirecionar}" itemValue="/prodocente/producao/relatorio/seleciona_docente.jsf"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Opera��es do Chefe" rendered="#{acesso.chefeDepartamento}">
			<t:navigationMenuItem itemLabel="Est�gio"/>
			<t:navigationMenuItem itemLabel="Qualifica��o"/>
			<t:navigationMenuItem itemLabel="Monografia"/>
			<t:navigationMenuItem itemLabel="Mini-Curso"/>
			<t:navigationMenuItem itemLabel="Validar Produ��es" action="validacao"/>
		</t:navigationMenuItem>


	</t:navigationMenuItem>


	<t:navigationMenuItem itemLabel="Extens�o" id="extensao" icon="/img/icones/extensao_menu.gif">

		<t:navigationMenuItem itemLabel="Atividades de Extens�o">
				<t:navigationMenuItem itemLabel="Submeter proposta" actionListener="#{menuDocente.redirecionar}" itemValue="/extensao/Atividade/form.jsf"/>
				<t:navigationMenuItem itemLabel="Verificar andamento da proposta"/>
				<t:navigationMenuItem itemLabel="Solicitar renova��o" action="#{atividadeExtensao.listarAtividadesRenovacao}"/>
				<t:navigationMenuItem itemLabel="Consultar Atividade" action="#{atividadeExtensao.preLocalizar}"/>
				<t:navigationMenuItem itemLabel="Listar minhas atividades" action="#{atividadeExtensao.listByMembro}" split="true"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Relat�rios">
			<t:navigationMenuItem itemLabel="Avaliar Relat�rio do Bolsista"/>
			<t:navigationMenuItem itemLabel="Enviar Relat�rio do Coordenador"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Editais"/>

	</t:navigationMenuItem>





	<t:navigationMenuItem itemLabel="Portais" icon="/img/icones/coordenacao_menu.gif">
		<t:navigationMenuItem itemLabel="Portal Docente" actionListener="#{menuDocente.redirecionar}" itemValue="/portais/docente/docente.jsf"/>
		<t:navigationMenuItem itemLabel="Coordenador Gradua��o"  itemValue="/verMenuGraduacao.do" actionListener="#{menuDocente.redirecionar}" split="true"/>
		<t:navigationMenuItem itemLabel="Coordenador Lato Sensu" disabled="#{acesso.coordenador}" action="coordenadorLato"/>
		<t:navigationMenuItem itemLabel="Coordenador Stricto Sensu"/>
	</t:navigationMenuItem>
</t:jscookMenu>
</h:form>

</div>
</div>
