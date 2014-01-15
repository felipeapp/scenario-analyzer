<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="designacaoFuncaoProjetoMBean" />
	<h2><ufrn:subSistema /> &gt; Designar Função para Membro </h2>
	<h:form>
	
	<table class="formulario" style="width: 49%">
		
		<caption>Cadastre a Função para o Membro</caption>
		
		<tbody>

			<tr>
				<th style="font-weight: bold;"> Projeto: </th>
				<td> <h:outputText value="#{ designacaoFuncaoProjetoMBean.projeto.titulo }"/> </td>
			</tr>

			<tr>
				<th class="obrigatorio"> Tipo de Designação: </th>
				<td> 
					<h:selectOneMenu value="#{ designacaoFuncaoProjetoMBean.obj.tipoDesignacao.id }" id="tipoDesginacao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{ tipoDesignacaoFuncaoProjetoMBean.allCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
		
			<tr>
				<th class="obrigatorio" width="30%"> Membro: </th>
				<td>
					<h:selectOneMenu value="#{ designacaoFuncaoProjetoMBean.obj.membroProjeto.id }" id="membroProjeto">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{ designacaoFuncaoProjetoMBean.allMembros }"/>
					</h:selectOneMenu>
				</td>
			</tr>

		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="#{designacaoFuncaoProjetoMBean.confirmButton}" 
						action="#{designacaoFuncaoProjetoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{designacaoFuncaoProjetoMBean.cancelar}" 
						id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
		
	</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>