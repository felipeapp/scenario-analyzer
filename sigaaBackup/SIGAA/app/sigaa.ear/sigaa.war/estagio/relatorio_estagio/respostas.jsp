<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="relatorioEstagioMBean" />
<a4j:keepAlive beanName="buscaEstagioMBean"/>
<h2> <ufrn:subSistema /> &gt; Respostas do Relatório</h2>		

<table class="visualizacao">
	<caption>Dados do Relatório</caption>
	<tr>
		<th width="30%">Preenchido por:</th>
		<td>
			${relatorioEstagioMBean.obj.registroCadastro.usuario.pessoa.nome} em <ufrn:format type="data" valor="${relatorioEstagioMBean.obj.dataCadastro}"/>
		</td>
	</tr>
	<tr>
		<th>Data de Preenchimento:</th>
		<td><ufrn:format type="data" valor="${relatorioEstagioMBean.obj.dataCadastro}"/></td>
	</tr>
	<c:if test="${not empty relatorioEstagioMBean.obj.registroAprovacao.usuario.pessoa.nome}">
		<tr>
			<th class="rotulo">Aprovado Por:</th>
			<td>${relatorioEstagioMBean.obj.registroAprovacao.usuario.pessoa.nome}</td>
		</tr>
		<tr>
			<th>Data de Aprovação:</th>
			<td><ufrn:format type="data" valor="${relatorioEstagioMBean.obj.dataAprovacao}"/></td>
		</tr>
	</c:if>
	<tr>
		<td colspan="2" class="subFormulario">Respostas</td>
	</tr>
	<tr>
		<td colspan="2">
			<%@include file="/geral/questionario/_respostas.jsp"%>		
		</td>
	</tr>
	<tfoot>
		<tr>
			<td align="center" colspan="2">
				<h:form>
					<h:commandButton value="<< Voltar" action="#{relatorioEstagioMBean.view}" id="btVoltar"/>
				</h:form>
			</td>
		</tr>
	</tfoot>											
</table>	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>