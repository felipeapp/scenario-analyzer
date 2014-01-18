<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmSimples"
		value="return confirm('Deseja cancelar a operação?');"
		scope="request" />
<f:view>
<style>

table.listagem {
	width: 700px;
}

.listagem td.direita { 
	text-align: right;
}



</style>


	<h2><ufrn:subSistema /> &gt; Cadastrar Usuário para Coordenador de Unidade</h2>
 <h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" height="16" width="16"/> : Cadastrar Usuário
		</div>
	</center>
	
	<table class="listagem">
		<caption>Lista de Coordenadores de Unidade do Campus</caption>
	</table>
	
  	<t:dataTable id="dtCoordenadores"  value="#{usuarioCoordenadorUnidadeMBean.coordenadoresCampus}" 
 		var="coord" binding="#{usuarioCoordenadorUnidadeMBean.coordenadoresPossiveis}" 
 		width="70%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" forceId="true" forceIdIndex="true">
 		
		<t:column>
			<f:facet name="header">
				<f:verbatim>Docente</f:verbatim>
			</f:facet>
			<h:outputText value="#{coord.pessoa.nome}"/>
		</t:column>
		
		<t:column>
			<f:facet name="header">
				<f:verbatim>Curso</f:verbatim>
			</f:facet>
			<h:outputText value="#{coord.dadosCurso.curso.nome}"/>
		</t:column>
		
		<t:column styleClass="direita">
			<h:commandButton action="#{usuarioCoordenadorUnidadeMBean.selecionaCoordenador}" image="/img/seta.gif" value="Cadastrar Usuário" id="btCadastrarUsuario" title="Cadastrar Usuário"/>
		</t:column>
		
 	</t:dataTable>
 	
 	<table class="listagem">
		<tfoot>
			<tr>
				<td align="center">
					<h:commandButton action="#{usuarioCoordenadorUnidadeMBean.voltarTelaListaCampus}"  value="<< Voltar" id="btVoltarTelaCampus" title="Voltar"/>
					<h:commandButton action="#{usuarioCoordenadorUnidadeMBean.cancelar}"  value="Cancelar" id="btCancelar" title="Cancelar" onclick="#{confirmSimples}"/>
				</td>
			</tr>
		</tfoot>
	</table>
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>