<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.centerAlign{ text-align: center; }
 	.leftAlign  { text-align: left; }
</style>

<f:view>
	<a4j:keepAlive beanName="implantarHistoricoMedioMBean"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> &gt; Implantar Histórico > Resumo</h2>

	<c:set var="discente" value="#{implantarHistoricoMedioMBean.discente}" />
	<%@include file="/medio/discente/info_discente.jsp"%>		
	<br/>

	<h:form>
	<table class="formulario" width="100%">
	<caption>Matrículas Cadastradas</caption>

	<tr><td>
		<t:dataTable value="#{implantarHistoricoMedioMBean.matriculas}" var="mat"
			rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculas" rowIndexVar="linha">

			<h:column></h:column>
			<h:column  headerClass="colCenter">
				<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
				<h:outputText value="#{mat.ano}"/>
			</h:column>

			<h:column  headerClass="colLeft">
				<f:facet name="header"><f:verbatim>Série</f:verbatim></f:facet>
				<h:outputText value="#{mat.serie.descricaoCompleta}"/>
			</h:column>					

			<h:column  headerClass="colLeft">
				<f:facet name="header"><f:verbatim>Disciplina</f:verbatim></f:facet>
				<h:outputText value="#{mat.componente.nome}"/>
			</h:column>

			<h:column headerClass="colRight">
				<f:facet name="header"><f:verbatim>${ implantarHistoricoMedioMBean.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
				<h:outputText value="#{ implantarHistoricoMedioMBean.conceito ? mat.conceitoChar : implantarHistoricoMedioMBean.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
			</h:column>
			<h:column><div style="margin-right:10px"></div></h:column>

			<h:column >
				<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
				<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
			</h:column>
		</t:dataTable>
	</td></tr>
	<tr><td>
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	</td></tr>
	<tfoot>
		<tr>
			<td>
				<h:commandButton value="Cadastrar" action="#{implantarHistoricoMedioMBean.cadastrar}" id="btnCadastrar"/>
				<h:commandButton value="<< Voltar" action="#{implantarHistoricoMedioMBean.voltar}"  id="btnVolta"/>
				<h:commandButton value="Cancelar" action="#{implantarHistoricoMedioMBean.cancelar}" id="btnCancelar" onclick="#{confirm}" immediate="true" />
			</td>
		</tr>
	</tfoot>
	</table>
	
		
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
