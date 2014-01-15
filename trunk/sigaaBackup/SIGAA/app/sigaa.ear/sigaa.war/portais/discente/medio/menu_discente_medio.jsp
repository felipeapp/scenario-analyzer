<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%-- MENU DE OP��ES PARA O DISCENTE --%>

<div id="menu-dropdown">
<div class="wrapper">

<h:form id="form_menu_discente">

<input type="hidden" name="id" value="${ usuario.discenteAtivo.id }"/>
<input type="hidden" name="jscook_action"/>

<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		
		<t:navigationMenuItem id="boletim" itemLabel="Boletim" action="#{ boletimMedioMBean.iniciarDiscente }" itemDisabled="false" icon=""/>
		<t:navigationMenuItem id="atestadoMatricula" itemLabel="Atestado de Matr�cula" action="#{ portalDiscenteMedio.atestadoMatricula }" itemDisabled="false" icon="/img/celular_icone.gif"/>
		<t:navigationMenuItem id="declaracaoVinculo" itemLabel="Declara��o de V�nculo" action="#{ declaracaoVinculo.emitirDeclaracao}" itemDisabled="false" />
		<t:navigationMenuItem id="consultarHistorico" itemLabel="Consultar Hist�rico" action="#{ portalDiscenteMedio.historico }" itemDisabled="false"/>
		 	
		<t:navigationMenuItem id="consultarDisciplina" itemLabel="Consultar Disciplina" action="#{ buscaDisciplinaMedio.popularBuscaGeral }" itemDisabled="false"/>
		
		<t:navigationMenuItem id="consultarTurma" itemLabel="Consultar Turma" action="#{turmaSerie.listar}" itemDisabled="false"/>
		
		<t:navigationMenuItem id="consultarCalendarioAcademico" itemLabel="Consultar Calend�rio Acad�mico" action="#{calendario.iniciarBusca}" itemDisabled="false" split="true"/>
		
	</t:navigationMenuItem>

	<%-- PESQUISA --%>
	<t:navigationMenuItem itemLabel="Pesquisa" id="pesquisa" icon="/img/icones/pesquisa_menu.gif">
		<t:navigationMenuItem id="consultarProjetos" itemLabel="Consultar Projetos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true"/>
			<t:navigationMenuItem id="planoTrabalho" itemLabel="Plano de Trabalho" split="true">
				<t:navigationMenuItem id="meusPlanosTrabalho" itemLabel="Meus Planos de Trabalho" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/pesquisa/planoTrabalho/wizard.do?dispatch=listarPorDiscente"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="report_iniciacaoCientifica" itemLabel="Relat�rios de Inicia��o Cient�fica">
				<t:navigationMenuItem id="relatoriosParciais" itemLabel="Relat�rios Parciais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaParcial" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaParcial" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="relatoriosFinais" itemLabel="Relat�rios Finais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaFinal" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaFinal" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="certificadosDeclaracoes" itemLabel="Certificados e Declara��es">
				<t:navigationMenuItem id="declaracoesBolsista" itemLabel="Declara��o de Bolsista em Plano de Trabalho" action="#{declaracoesPesquisa.listarDeclararoesPlanoTrabalhoBolsista}" />				
			</t:navigationMenuItem>
		<t:navigationMenuItem id="congressoIniciacaoCientifica" itemLabel="Congresso de Inicia��o Cient�fica">
			<t:navigationMenuItem id="submeterResumoCongresso" itemLabel="Submeter resumo" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=popularInicioEnvio"/>
			<t:navigationMenuItem id="resumosCongresso" itemLabel="Meus resumos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=listarResumosAutor"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<%-- EXTENS�O --%>
	<t:navigationMenuItem itemLabel="Extens�o" id="extensao" icon="/img/icones/extensao_menu.gif" rendered="#{(!usuario.discenteAtivo.medio) }">
		
			<t:navigationMenuItem id="consultarAcoesExtensao" itemLabel="Consultar A��es" action="#{atividadeExtensao.preLocalizar}" itemDisabled="false" />
			<t:navigationMenuItem id="meusPlanosTrabalhoExtensao" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoExtensao.carregarPlanosTrabalhoDiscenteLogado }" split="true"/>
			<t:navigationMenuItem id="acoesMembroEquipeExtensao" itemLabel="Minhas A��es como Membro da Equipe" action="#{ atividadeExtensao.carregarAcoesDiscenteLogado }"/>			
			<t:navigationMenuItem id="relatoriosExtensao" itemLabel="Meus Relat�rios" action="#{ relatorioBolsistaExtensao.iniciarCadastroRelatorio }"/>
			<t:navigationMenuItem id="certificadosDeclaracoesExtensao" itemLabel="Certificados e Declara��es" action="#{documentosAutenticadosExtensao.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="inscreverAcaoExtensao" itemLabel="Inscri��o On-line em A��es de Extens�o" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/link/public/extensao/inscricoesOnline" itemDisabled="false"/>
			<t:navigationMenuItem id="visualizarResultadosExtensao" itemLabel="Visualizar Resultados das inscri��es" action="#{selecaoDiscenteExtensao.iniciarVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	<%-- MONITORIA --%>
	<t:navigationMenuItem itemLabel="Monitoria" id="monitoria" icon="/img/icones/monitoria_menu.gif" rendered="#{(usuario.discenteAtivo.graduacao)}">
	
		<t:navigationMenuItem id="consultarProjetosMonitoria" itemLabel="Consultar Projetos" action="#{coordMonitoria.situacaoProjeto}" />
		<t:navigationMenuItem id="projetosMonitoria" itemLabel="Meus Projetos de Monitoria" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false" split="true"/>
		<t:navigationMenuItem id="relatoriosMonitoria" itemLabel="Meus Relat�rios" action="#{ relatorioMonitor.listar }"/>
		<t:navigationMenuItem id="certificadosMonitoria" itemLabel="Meus Certificados">
			<t:navigationMenuItem id="certificadosProjetosMonitoria" itemLabel="Certificados de Projetos" action="#{documentosAutenticadosMonitoria.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="certificadosSID_Monitoria" itemLabel="Certificados do SID" action="#{resumoSid.listarParticipacoesDiscente}" itemDisabled="false" />
		</t:navigationMenuItem>			
		<t:navigationMenuItem id="atividadesMesFrequenciaMonitoria" itemLabel="Atividades do M�s / Freq��ncia">
			<t:navigationMenuItem id="cadastrarAtividadesMonitoria" itemLabel="Cadastrar" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false"/>
			<t:navigationMenuItem id="consultarAtividadesMonitoria" itemLabel="Consultar" action="#{atividadeMonitor.listarAtividades}" itemDisabled="false" split="true"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="inscreverSelecaoMonitoria" itemLabel="Inscrever-se em Sele��o de Monitoria" action="#{agregadorBolsas.iniciarBuscar}" itemDisabled="false"/>
		<t:navigationMenuItem id="resultadoSelecaoMonitoria" itemLabel="Visualizar Resultado da Sele��o" action="#{discenteMonitoria.popularVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	
    <%-- A��O ACAD�MICA ASSOCIADA --%>
    <t:navigationMenuItem itemLabel="A��es Associadas" id="acoesAssociadas" icon="/img/projetos/associado_menu.png" rendered="#{(!usuario.discenteAtivo.medio) }">
		<t:navigationMenuItem id="consultarAcoesAssociadas" itemLabel="Consultar A��es Associadas" action="#{ buscaAcaoAssociada.iniciar }" itemDisabled="false" />
		<t:navigationMenuItem id="meusPlanosTrabalhoAssociadas" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoProjeto.planosDiscente }" split="true"/>
    </t:navigationMenuItem> 	
	

	<%-- ACESSO AOS SERVI�OS DA BIBLIOTECA PARA O DISCENTE --%>

	<%-- o aluno estando ativo � para aparecer essa op��o no menu --%>
	<t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif">
		
		<t:navigationMenuItem id="bib_PesqMaterialAcervo" action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" itemLabel="Pesquisar Material no Acervo" />
		<t:navigationMenuItem id="bib_PesqArtigoAcervo" action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" itemLabel="Pesquisar Artigo no Acervo" />
		
			
		<t:navigationMenuItem id="bib_emprestimos" itemLabel="Empr�stimos">
			<t:navigationMenuItem id="bib_renovarEmprestimos" itemLabel="Renovar Meus Empr�stimos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_historicoEmprestimos" itemLabel="Meu Hist�rico de Empr�stimos" action="#{emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado}" itemDisabled="false" />
			 <t:navigationMenuItem id="bib_Emitir_GRU" itemLabel="Imprimir GRU para pagamentos de multas" action="#{emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas}" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		
		<t:navigationMenuItem id="bib_dessiminacao_informacao" itemLabel="Dissemina��o Seletiva da Informa��o" split="true">
			<t:navigationMenuItem id="bib_cadastrar_interesse" itemLabel="Cadastrar Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_verificarSituacao" itemLabel="Verificar minha Situa��o / Emitir Documento de Quita��o  " action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado}" itemDisabled="false" />
		
		<t:navigationMenuItem id="bib_informacoes_usuario" itemLabel="Informa��es ao Usu�rio">
			<t:navigationMenuItem id="bib_visualizarVinculos" itemLabel="Visualizar meus V�nculos no Sistema " action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Visualizar_Politicas" itemLabel="Visualizar as Pol�ticas de Empr�stimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_revervas" itemLabel="Reservas de Materiais" split="true" rendered="#{solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}"> 
			<t:navigationMenuItem id="bib_revervas_visualizar" itemLabel="Visualizar Minhas Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas}" itemDisabled="false"  />
			<t:navigationMenuItem id="bib_revervas_solicitar" itemLabel="Solicitar Nova Reserva " action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario}" itemDisabled="false"  />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_solicitacoes" itemLabel="Solicita��es">
			<t:navigationMenuItem id="bib_SolOrientacao" itemLabel="Agendamento de Orienta��o" action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolNormalizacao" itemLabel="Normaliza��o" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolCatalocacao" itemLabel="Cataloga��o na Fonte" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" />
	    </t:navigationMenuItem>
        
		<t:navigationMenuItem id="bib_AcomCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Acompanhar Solicita��o de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}" split="true"/>			
		<t:navigationMenuItem id="bib_SolCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Solicitar Compra de Livros para a Biblioteca" itemValue="/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}"/>
		
		<t:navigationMenuItem id="bib_cadServicosBiblioteca" itemLabel="Cadastrar para Utilizar os Servi�os da Biblioteca" action="#{cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro}" icon="/img/biblioteca/novo_usuario.gif" itemDisabled="false" split="true"/>
	</t:navigationMenuItem>	


	
	<%-- ACESSO AOS SERVI�OS DE BOLSAS PARA O DISCENTE --%>
	
	<t:navigationMenuItem id="bolsas" itemLabel="Bolsas" icon="/img/bolsas.png">
	
		<t:navigationMenuItem id="bolsa_cadastroUnico" itemLabel="Aderir ao Cadastro �nico" action="#{ adesaoCadastroUnico.apresentacaoCadastroUnico }"	/>	
			
		<t:navigationMenuItem id="bolsa_oportunidades" itemLabel="Oportunidades de Bolsa" action="#{ agregadorBolsas.iniciarBuscar }" split="true" />
			
		<t:navigationMenuItem id="bolsa_registrosInteresse" itemLabel="Acompanhar Meus Registros de Interesse" action="#{interessadoBolsa.acompanharInscricoes}" />
		
		<t:navigationMenuItem id="bolsa_minhasBolsasInstituicao" itemLabel="Minhas Bolsas na Institui��o" action="#{ relatorioBolsasDiscenteBean.listarBolsasInstituicao }" />

		<t:navigationMenuItem id="bolsa_auxilioAlimentacao" itemLabel="Solicita��o de Bolsa Aux�lio Alimenta��o" rendered="#{(usuario.discenteAtivo.graduacao)}"
		 action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaAlimentacao }" split="true" />
	
		<t:navigationMenuItem id="bolsa_auxilioResidencia" itemLabel="Solicita��o de Bolsa Aux�lio Resid�ncia" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaResidencia }" rendered="#{(usuario.discenteAtivo.graduacao)}"/>
	
		<t:navigationMenuItem id="bolsa_auxilioTransporte" itemLabel="Solicita��o de Bolsa Aux�lio Transporte/CERES" action="#{ bolsaAuxilioMBean.abrirTelaAvisoBolsaTransporte }" rendered="#{(usuario.discenteAtivo.graduacao)}"/>
	
		<t:navigationMenuItem id="bolsa_acompanharAuxilios" itemLabel="Acompanhar Solicita��o de Bolsa Aux�lio" action="#{ bolsaAuxilioMBean.acompanharSituacaoBolsaAuxilio }" rendered="#{(usuario.discenteAtivo.graduacao)}"/>
	
		<t:navigationMenuItem id="bolsa_planoDocenciaAssistida" itemLabel="Plano de Doc�ncia Assistida" action="#{ planoDocenciaAssistidaMBean.iniciar }" split="true"
			rendered="#{ usuario.discenteAtivo.stricto}"  />
	</t:navigationMenuItem>	
	
	<%-- AMBIENTES VIRTUAIS --%>
	<t:navigationMenuItem id="ambientesVirtuais" itemLabel="Ambientes Virtuais" icon="/img/icones/amb_virt.png">
		<t:navigationMenuItem itemLabel="Comunidades Virtuais" id="comunidadeVirtual">
			<t:navigationMenuItem id="comunidadeVirtual_BuscarComunidade" actionListener="#{ buscarComunidadeVirtualMBean.criar }" itemLabel="Buscar Comunidades Virtuais"/>
			<t:navigationMenuItem id="comunidadeVirtual_MinhasComunidades" action="#{ buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente }" itemLabel="Minhas Comunidades"/>		
		</t:navigationMenuItem>
	</t:navigationMenuItem>	
	
	<t:navigationMenuItem id="menuOutros" itemLabel="Outros" icon="/img/menu/outros.png">
	
		<t:navigationMenuItem id="coordenacaoCurso" itemLabel="Coordena��o de Curso" >
			<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
				<t:navigationMenuItem id="atendimentoAluno" itemLabel="Atendimento ao Aluno" action="#{ atendimentoAluno.novaPergunta }"/>
			</c:if>
			<t:navigationMenuItem id="forumCursos" itemLabel="F�rum de Cursos" action="#{ forum.listarForunsCurso }" itemDisabled="false"/>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ouvidoria" itemLabel="Ouvidoria">
			<t:navigationMenuItem id="entrarEmContato" itemLabel="Entrar em Contato" action="#{ manifestacaoDiscente.preCadastrar }" itemDisabled="false"/>
			<t:navigationMenuItem id="acompanharManifestacoes" itemLabel="Acompanhar Manifesta��es" action="#{ analiseManifestacaoDiscente.listarManifestacoes }" itemDisabled="false"/>
		</t:navigationMenuItem>
		
		
		
		<t:navigationMenuItem id="senhaAcessoCelular" itemLabel="Criar senha de acesso por Celular" action="#{ senhaMobileMBean.iniciar }" icon="/img/celular_icone.gif" split="true"/>
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
			<t:navigationMenuItem id="consultarProcessos" itemLabel="Consultar Processos" action="#{ consultaProcesso.iniciar }" />
		</c:if>
		
	</t:navigationMenuItem>

</t:jscookMenu>

</h:form>

</div>
</div>