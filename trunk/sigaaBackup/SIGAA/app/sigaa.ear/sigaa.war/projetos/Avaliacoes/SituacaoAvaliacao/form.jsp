<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>
<a4j:keepAlive beanName="situacaoAvaliacaoBean" />
<h:form>
	<h2><ufrn:subSistema /> &gt; Situação Avaliação</h2>
	<br />
	
	<table class="formulario" width="50%">
		
			<caption>${ situacaoAvaliacaoBean.confirmButton == 'Cadastrar' ? 'Cadastro' : 'Alteração' } de Situação de Avaliação</caption>
			<h:inputHidden value="#{ situacaoAvaliacaoBean.confirmButton }"/>
			
			<tr>
				<th class="obrigatorio">Descrição</th>
				<td>
					<h:inputText size="80" readonly="#{ situacaoAvaliacaoBean.readOnly }" value="#{ situacaoAvaliacaoBean.obj.descricao }" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{ situacaoAvaliacaoBean.confirmButton }" action="#{ situacaoAvaliacaoBean.cadastrar }" 
							id="btnConfirmar"  />
						<h:commandButton value="Cancelar" action="#{ situacaoAvaliacaoBean.toLista }" id="btnCancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
	</table>
	<br />
	<div class="obrigatorio">Campos de preenchimento obrigatório</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>