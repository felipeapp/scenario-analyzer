<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="estornoConvocacaoVestibularMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Estornar Cadastramento/Cancelamento de Convoca��o de Discentes</h2>
<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p> Confirme os dados abaixo. O Discente selecionado voltar� a ter o status de <b>PENDENTE DE CADASTRO</b> </p>
</div>

<h:form>

<table class="formulario" width="80%">
	<caption>Dados da Convoca��o</caption>
	<tr>
		<th class="rotulo" class="rotulo" width="25%">Processo Seletivo Vestibular:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.processoSeletivo.nome }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Convoca��o:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.convocacaoProcessoSeletivo.descricao }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Data da Convoca��o:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.convocacaoProcessoSeletivo.dataConvocacao }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Convocado Por:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.convocadoPor.usuario.nome }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Data do Cancelamento:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.cancelamento.dataCancelamento }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Motivo do Cancelamento:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.cancelamento.motivo.descricao }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Matr�cula:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.discente.matricula }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Matr�cula Antiga:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.discente.discente.matriculaAntiga }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Nome:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.discente.nome }" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">Matriz Curricular:</th>
		<td>
			<h:outputText value="#{ estornoConvocacaoVestibularMBean.obj.discente.matrizCurricular.descricao }" />
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Confirmar" action="#{estornoConvocacaoVestibularMBean.confirmaEstorno}"/>
				<h:commandButton value="Cancelar" action="#{ estornoConvocacaoVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>