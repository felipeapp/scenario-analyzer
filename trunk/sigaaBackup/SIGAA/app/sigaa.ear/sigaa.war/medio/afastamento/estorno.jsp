<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="afastamentoDiscenteMedioMBean"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Estorno de Afastamento</h2>
	<div class="descricaoOperacao">
		<p>
		Caro Usu�rio,
		</p><br>
		<p>
		Essa opera��o tem como objetivo estornar algum afastamento realizado de forma errada. O usu�rio seleciona o status que o
		aluno deve possuir ap�s o estorno,  e por exemplo no caso do estorno de um trancamento de programa, suas matr�culas s�o recuperadas e deixam
		 de ser canceladas.
		</p>
	</div>
	<c:set var="discente" value="#{afastamentoDiscenteMedioMBean.obj.discente}" />
	<%@include file="/medio/discente/info_discente.jsp"%>	
	
	<table class="formulario" width="600px">
		<h:form id="formulario">
			<caption class="listagem">Confirme o Estorno</caption>
			<tr>
				<th style="font-weight: bold;">�ltimo Afastamento</th>
				<td><h:outputText value="#{afastamentoDiscenteMedioMBean.obj.tipoMovimentacaoAluno.descricao}" /></td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Observa��o</th>
				<td><h:outputText value="#{afastamentoDiscenteMedioMBean.obj.observacao}" /></td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Ano de Refer�ncia</th>
				<td><h:outputText value="#{afastamentoDiscenteMedioMBean.obj.anoReferencia}"/></td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Data da Ocorr�ncia</th>
				<td><ufrn:format type="data" valor="${afastamentoDiscenteMedioMBean.obj.dataOcorrencia}" /></td>
			</tr>
			<c:if test="${ not empty afastamentoDiscenteMedioMBean.obj.dataCadastroRetorno}">
			<tr>
				<td colspan="2" align="center"><b>Este afastamento j� foi retornado!</b></td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Data de retorno</th>
				<td><ufrn:format type="data" valor="${afastamentoDiscenteMedioMBean.obj.dataCadastroRetorno}" /></td>
			</tr>
			<tr>
				<th style="font-weight: bold;">Usu�rio que retornou</th>
				<td><h:outputText value="#{afastamentoDiscenteMedioMBean.obj.usuarioRetorno.nome}" /></td>
			</tr>
			
			</c:if>
			<tr>
				<th class="required">Status que aluno deve ficar</th>
				<td>
					<h:selectOneMenu value="#{afastamentoDiscenteMedioMBean.statusRetorno}" id="status" style="width: 40%;">
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discenteMedio.statusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Confirmar" id="btnConfirmar" action="#{afastamentoDiscenteMedioMBean.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar" action="#{afastamentoDiscenteMedioMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
