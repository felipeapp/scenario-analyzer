<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Dados Acad�micos Consolidados</h2>
	<br>

	<h:form id="form">
	
	<h:messages showDetail="true"/>
		<table class="formulario" width="30%">

			<caption class="listagem">Consolida��o dos Dados Acad�micos</caption>

			<tr>
				<th class="obrigatorio"> Ano: </th> 
				<td> 
					<h:inputText value="#{ dadosIndiceAcaMBean.obj.anoReferencia }" maxlength="4" size="4" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Consolidar �ndices" action="#{dadosIndiceAcaMBean.importarDados}"/>
					<h:commandButton value="Cancelar" action="#{dadosIndiceAcaMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>

		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>