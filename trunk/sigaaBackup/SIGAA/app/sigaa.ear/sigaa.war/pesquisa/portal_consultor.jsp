<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/tecnico/consultor.css"/>

<f:view>
	<h2> <ufrn:subSistema /> </h2>

	<%-- Instruções para o consultor --%>
	<div class="descricaoOperacao">
		<p style="margin-bottom: 5px;">
			Caro consultor,
		</p>
		<p>
			Através deste portal você poderá realizar as avaliações dos projetos de pesquisa, planos de trabalho
			e relatórios finais de projeto que a você foram destinados.
		</p>
		<p>
			Para cada projeto será apresentado seu texto completo e ao final um formulário com os itens
			da avaliação da qualidade técnica do projeto, que poderá ser confirmada em uma tela final de resumo.
		</p>
	</div>
	
	<%-- Opções de matrícula --%>
	<h:form>
	<div id="opcoes-consultor">
		<h3> Consultas </h3>
					
			<div class="item relatorioic">
				<h:commandLink action="#{consultaResumoCongressoMBean.iniciar}">
					Resumos CIC
				</h:commandLink>
			</div>
<%-- 
			<div class="item projetosnavaliados">
				<h:commandLink action="#{portalConsultorMBean.listarProjetos}">
					Projetos, Planos e Relatórios
				</h:commandLink>				
			</div>
--%>			
			<div class="item relatorioic">
				<h:commandLink action="#{portalConsultorMBean.telaRelatoriosFinais}">
					Relatórios Finais de IC
				</h:commandLink>
			</div>
			<div class="clear"> </div>
			
		<h3> Avaliações </h3>
			<div class="item planos">
				<h:commandLink action="#{portalConsultorMBean.telaPlanoPendentes}">
					Planos de Trabalho Pendentes
				</h:commandLink>
			</div>		
			<div class="item projetosnavaliados">
				<h:commandLink action="#{portalConsultorMBean.telaProjetosPendentes}">
					Projetos de Pesquisa Pendentes
				</h:commandLink>				
			</div>
	
	
			<div class="item relatorios">
				<h:commandLink action="#{portalConsultorMBean.telaRelatorios}">
					Relatórios de Projeto
				</h:commandLink>
			</div>
	
			<div class="item projetoavaliados">
				<h:commandLink action="#{portalConsultorMBean.telaProjetosAvaliados}">
					Projetos de Pesquisa Avaliados
				</h:commandLink>
			</div>

			
			<div class="item planos">
				<h:commandLink action="#{portalConsultorMBean.telaPlanosConsultoriaEspecial}">
					Planos Consultoria Especial
				</h:commandLink>
			</div>
			<div class="item projetosnavaliados">
				<h:commandLink action="#{portalConsultorMBean.telaProjetosConsultoriaEspecial}">
					Projetos Consultoria Especial
				</h:commandLink>
			</div>

			<div class="clear"> </div>

		<h3> Outras opções </h3>
		
		<div class="item comprovante">
			<html:link action="/pesquisa/emissaoCertificadoConsultor?dispatch=emitir&obj.id=${sessionScope.usuario.consultor.id}&propesq=false">
				Certificado de Consultoria
			</html:link>
		</div>

	</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>