<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Relat�rio do Est�gio</h2>

<f:view>	
	<a4j:keepAlive beanName="relatorioEstagioMBean"/>
	<a4j:keepAlive beanName="buscaEstagioMBean"/>
	<h:form id="form">
	<div class="descricaoOperacao">
		<p><b>Prezado Usu�rio,</b></p><br />
		<p>� necess�rio preenchimento do referido Relat�rio referente ao est�gio.</p>
	</div>
	
	<c:set var="estagio" value="#{relatorioEstagioMBean.obj.estagio}"/>
	<%@include file="/estagio/estagio/include/_dados_estagio.jsp" %>
	<br/>
	
	<table class="formulario" width="100%">
		<caption>Responda as perguntas abaixo</caption>
	</table>
	<%@include file="/geral/questionario/_formulario_respostas.jsp" %>
	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" id="submeterRelatorio" action="#{relatorioEstagioMBean.submeterRelatorio}" /> 
					<h:commandButton value="<< Voltar" action="#{buscaEstagioMBean.telaBusca}" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{relatorioEstagioMBean.cancelar}" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>