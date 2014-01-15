<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/tecnico/consultor.css"/>

<f:view>
	<h2> <ufrn:subSistema /> </h2>

	<%-- Instru��es para o consultor --%>
	<div class="descricaoOperacao">
		<p style="margin-bottom: 5px;">
			Caro consultor,
		</p>
		<p>
			Atrav�s deste portal voc� poder� realizar as avalia��es dos projetos de pesquisa, planos de trabalho
			e relat�rios finais de projeto que a voc� foram destinados.
		</p>
		<p>
			Para cada projeto ser� apresentado seu texto completo e ao final um formul�rio com os itens
			da avalia��o da qualidade t�cnica do projeto, que poder� ser confirmada em uma tela final de resumo.
		</p>
	</div>
	
	<%-- Op��es de matr�cula --%>
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
					Projetos, Planos e Relat�rios
				</h:commandLink>				
			</div>
--%>			
			<div class="item relatorioic">
				<h:commandLink action="#{portalConsultorMBean.telaRelatoriosFinais}">
					Relat�rios Finais de IC
				</h:commandLink>
			</div>
			<div class="clear"> </div>
			
		<h3> Avalia��es </h3>
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
					Relat�rios de Projeto
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

		<h3> Outras op��es </h3>
		
		<div class="item comprovante">
			<html:link action="/pesquisa/emissaoCertificadoConsultor?dispatch=emitir&obj.id=${sessionScope.usuario.consultor.id}&propesq=false">
				Certificado de Consultoria
			</html:link>
		</div>

	</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>