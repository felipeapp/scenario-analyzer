<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Caro Bibliotecário, </p>
		<p>Por favor, informe o motivo da solicitação não ser atendida.</p>
	</div>
	
	<h:form id="form">
		
		<table class="formulario" style="width: 80%">
			
			<caption>Insira o motivo do cancelamento</caption>
			
			<tbody>
				<tr>
					<th class="obrigatorio">Motivo:</th>
					<td>
						<h:inputTextarea id="motivo" rows="4"  cols="70" value="#{ levantamentoInfraMBean.obj.motivoCancelamento }" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 300);"/>
					</td>
				</tr>
				<tr>
					<th>Caracteres Restantes:</th>
					<td>
						<span id="quantidadeCaracteresDigitados">300</span>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>	
					<td colspan="2">
						<h:commandButton value="Invalidar Levantamento" id="invalidarLevantamento" action="#{ levantamentoInfraMBean.cancelarSolicitacaoBibliotecario }" />
						<h:commandButton value="<< Voltar" id="voltar" action="#{ levantamentoInfraMBean.visualizarParaBibliotecario }" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>
	
</f:view>


<SCRIPT LANGUAGE="JavaScript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>