<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
		<br />

		<table class="formulario" width="80%">
			<caption>Recalcular Discentes</caption>
			<tr>
				<th width="30%">C�lculos a realizar:</th>
				<td>
					<h:selectOneMenu id="opcaoCalculo" value="#{recalculosMBean.opcao}">
						<f:selectItem itemValue="todos" itemLabel="Todos (tipos, totais, status, ira)" />
						<f:selectItem itemValue="statusTipos" itemLabel="Status e Tipos de Integraliza��o" />
						<f:selectItem itemValue="consolidacao" itemLabel="Consolida��o (totais, status, ira)" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Zerar Integraliza��es:</th>
				<td>
					<h:selectOneRadio id="zerarIntegralizacoes" value="#{recalculosMBean.zerarIntegralizacoes}">
						<f:selectItems value="#{recalculosMBean.simNao}" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>N�mero de Threads:</th>
				<td><h:inputText value="#{ recalculosMBean.numThreads }" onkeyup="return formatarInteiro(this);" size="3" /></td>
			</tr>
			<tr>
				<th>Crit�rios para restri��o de discentes (opcional)
				<ufrn:help>
					Informe um crit�rio SQL para restri��o. <br />Exemplo: id_discente in (select ...)
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
						<h:commandButton value="Parar C�lculos" action="#{ recalculosMBean.pararCalculoDiscentes }" rendered="#{recalculosMBean.funcionando}"/>
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
