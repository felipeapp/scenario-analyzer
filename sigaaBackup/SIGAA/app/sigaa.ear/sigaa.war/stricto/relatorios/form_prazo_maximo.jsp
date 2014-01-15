<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h:outputText value="#{relatorioPrazoMaximoPosBean.create}" />
<h2> <ufrn:subSistema /> &gt; Relatório de Prazo Máximo </h2>

<a4j:keepAlive beanName="relatorioPrazoMaximoPosBean"/>
<h:form id="form">
<table class="formulario" style="width: 70%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>

	<h:inputHidden id="nomeRelatorio" value="#{relatorioPrazoMaximoPosBean.nomeRelatorio}" />	
	<tbody>
	<tr>
		<th class="${acesso.ppg?'required':''}">
			<c:choose>
				<c:when test="${!acesso.ppg}">
					<b>Programa:</b> 
				</c:when>
				<c:otherwise>
					Programa: 
				</c:otherwise>
			</c:choose>
		</th>
		<td>
		<c:if test="${acesso.ppg}">
			<h:selectOneMenu id="programa" value="#{relatorioPrazoMaximoPosBean.unidade.id}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${!acesso.ppg}">
			${relatorioPrazoMaximoPosBean.programaStricto}
		</c:if>
		</td>
	</tr>
	<tr>
		<th>Tipo de Discente:</th>
		<td>
			<h:selectOneMenu id="tipo_discente" value="#{relatorioPrazoMaximoPosBean.tipoDiscente}">
				<f:selectItem itemLabel="TODOS" itemValue="-1"/>
				<f:selectItem itemLabel="REGULAR" itemValue="1"/>
				<f:selectItem itemLabel="ESPECIAL" itemValue="2"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Formato:</th>
		<td>
			<h:selectOneRadio id="formato" value="#{relatorioPrazoMaximoPosBean.formato}">
				<f:selectItems value="#{relatorioPrazoMaximoPosBean.descricaoFormato }"/>
			</h:selectOneRadio>
		</td>
	</tr>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
		   	<h:commandButton id="btEmitir"  action="#{relatorioPrazoMaximoPosBean.exibirListagem}" value="Emitir Relatório"/>
			<h:commandButton id="btCancelar" action="#{relatorioPrazoMaximoPosBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br />
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<script>
	function mostrarBarra(){
		document.getElementById('form:exibirBarra').click(); 
		document.getElementById('form:btEmitir').disabled = 'disabled'; 
		document.getElementById('form:exibirListagem').style.display = 'none';
		document.getElementById('progress').style.display = 'block';
	}

	function esconderBarra(){
		//document.getElementById('form:exibirListagem').click(); 
		document.getElementById('form:btEmitir').disabled = '';
		document.getElementById('form:exibirListagem').style.display = 'block';
		document.getElementById('progress').style.display = 'none';
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>