<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Distribuição de projetos para avaliação</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{distribuicaoProjetoMbean.preCadastrar}" value="Cadastrar" id="lnkCadastrar"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover
				<h:graphicImage value="/img/table_go.png"style="overflow: visible;"/>: Selecionar Projetos
		</div>
	</center>
	
  	<h:dataTable id="dtDistribuicoes"  value="#{distribuicaoProjetoMbean.distribuicoesAtivas}" 
 		var="distribuicao" binding="#{distribuicaoProjetoMbean.distribuicoesCadastradas}" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Lista das Distribuições Cadastradas" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Modelo de avaliação</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.modeloAvaliacao.descricao}"/> <h:outputText value=" (#{distribuicao.modeloAvaliacao.tipoAvaliacao.descricao})" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Método</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.metodo == 'M'? 'MANUAL' : 'AUTOMÁTICA'}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Avaliador</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.tipoAvaliador.descricao}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim><center><h:outputText value="D" title="Distribuições" /></center></f:verbatim>
			</f:facet>
			<f:verbatim><center><h:outputText value="#{distribuicao.totalAvaliacoesDistribuidas}" /></center></f:verbatim>
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim><center><h:outputText value="AR" title="Avaliações Realizadas" /></center></f:verbatim>
			</f:facet>
			<f:verbatim><center><h:outputText value="#{distribuicao.totalAvaliacoesRealizadas}" /></center></f:verbatim>
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim><center><h:outputText value="AP" title="Avaliações Pendentes" /></center></f:verbatim>
			</f:facet>
			<f:verbatim><center><font color="red"><h:outputText value="#{distribuicao.totalAvaliacoesDistribuidas - distribuicao.totalAvaliacoesRealizadas}" /></font></center></f:verbatim>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{distribuicaoProjetoMbean.alterar}" image="/img/alterar.gif" value="Alterar" id="btAlterar" title="Alterar"/>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{distribuicaoProjetoMbean.inativar}" 
				onclick="return confirm('Tem certeza que deseja Remover esta distribuição?');"
				image="/img/delete.gif" value="Excluir" id="btRemover" title="Remover"/>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{distribuicaoProjetoMbean.selecionarProjetos}" image="/img/table_go.png" value="Selecionar Projetos" id="btSelecionarProjetos" title="Selecionar Projetos"/>
		</t:column>
		
 	</h:dataTable>
 	[D=Distribuições, AR=Avaliações Realizadas, AP=Avaliações Pendentes]
	<center><h:outputText  value="Não há distribuições cadastradas." rendered="#{empty distribuicaoProjetoMbean.distribuicoesAtivas}"/></center>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>