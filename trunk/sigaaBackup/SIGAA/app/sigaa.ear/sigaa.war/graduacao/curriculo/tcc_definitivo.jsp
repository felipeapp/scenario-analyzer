<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.area{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
-->
</style>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Estrutura Curricular &gt; Trabalhos de Conclus�o de Curso</h2>
	
	<div class="descricaoOperacao">
		<h3>Definir componente curricular do Trabalho de Conclus�o de Curso (TCC) definitivo</h3>
		<br/>
		<p>Escolha dentre os componentes listados abaixo aquele que ser� o TCC definitivo do curr�culo.
		O TCC definitivo � aquele no qual s�o registradas as informa��es de orienta��o e co-orienta��o (se houver) dos alunos.
		</p>
		<p>Os demais componentes curriculares ser�o	considerados automaticamente TCCs intermedi�rios.</p>
	</div>
	
	<h:form id="formulario">
		
		<table class="formulario" width="100%">
		    <caption> Trabalho(s) de Conclus�o de Curso </caption>
	 		<tbody>
				<tr>
					<td align="center">
						<h:selectOneRadio id="radioTccDefinitivo" layout="pageDirection" value="#{curriculo.idTccDefinitivo}">
							<f:selectItem itemLabel="NENHUM" itemValue="0"/>
							<f:selectItems value="#{curriculo.componentesTccItemList}"/>
						</h:selectOneRadio>
					
						<c:if test="${empty curriculo.componentesTcc}">
							<center>
								<i>N�o h� atividades do tipo <strong>Trabalho de Conclus�o de Curso</strong> cadastradas neste curr�culo.</i>
							</center>
						</c:if>
				</td>
			</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td align="center">
						<h:commandButton id="voltar" action="#{curriculo.voltarDadosGerais}" value="<< Dados Gerais" />
						<h:commandButton id="voltar2" action="#{curriculo.voltarComponentes}" value="<< Componentes" /> 
						<h:commandButton id="cancelar" action="#{curriculo.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
						<h:commandButton id="submissao" action="#{curriculo.submeterTccDefinitivo}" value="Pr�ximo Passo >>" />
					</td>
				</tr>
			</tfoot>
		 </table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>