<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
		<br />

		<table class="formulario" width="80%">
			<caption>Recalcular Discentes</caption>
			<tr>
				<th width="30%">Cálculos a realizar:</th>
				<td>
					<h:selectOneMenu id="opcaoCalculo" value="#{recalculosMBean.opcao}">
						<f:selectItem itemValue="todos" itemLabel="Todos (tipos, totais, status, ira)" />
						<f:selectItem itemValue="statusTipos" itemLabel="Status e Tipos de Integralização" />
						<f:selectItem itemValue="consolidacao" itemLabel="Consolidação (totais, status, ira)" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Zerar Integralizações:</th>
				<td>
					<h:selectOneRadio id="zerarIntegralizacoes" value="#{recalculosMBean.zerarIntegralizacoes}">
						<f:selectItems value="#{recalculosMBean.simNao}" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Número de Threads:</th>
				<td><h:inputText value="#{ recalculosMBean.numThreads }" onkeyup="return formatarInteiro(this);" size="3" /></td>
			</tr>
			<tr>
				<th>Critérios para restrição de discentes (opcional)
				<ufrn:help>
					Informe um critério SQL para restrição. <br />Exemplo: id_discente in (select ...)
				</ufrn:help>
				:</th>
				<td><h:inputTextarea value="#{ recalculosMBean.sqlRestricao }" rows="4" style="width: 95%;" /></td>
			</tr>			
			<tr>
				<th>Senha:</th>
				<td><h:inputSecret value="#{ recalculosMBean.senha }" size="10" /> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Recalcular" action="#{ recalculosMBean.recalcularDiscentes }" rendered="#{!recalculosMBean.funcionando}"/> 
						<h:commandButton value="Parar Cálculos" action="#{ recalculosMBean.pararCalculoDiscentes }" rendered="#{recalculosMBean.funcionando}"/>
						<h:commandButton value="Cancelar" action="#{ recalculosMBean.cancelar }" />
					</td>
				</tr>
			</tfoot>
		</table>

		<div style="width: 205px; margin: 20px auto;">
			<rich:progressBar label="#{ recalculosMBean.atualDiscente } de #{ recalculosMBean.totalDiscente }" 
				minValue="0" maxValue="#{ recalculosMBean.totalDiscente }"
				interval="2000" value="#{ recalculosMBean.atualDiscente }"
				enabled="#{ recalculosMBean.funcionando }">
				<f:facet name="complete">
					<br />
					<h:outputText value="Recalculos finalizados" />
				</f:facet>
			</rich:progressBar>
		</div>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
