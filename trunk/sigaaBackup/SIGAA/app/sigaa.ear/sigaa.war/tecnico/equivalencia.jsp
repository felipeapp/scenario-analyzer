<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<f:view>
	<p:resources/>
	<h2><ufrn:subSistema /> > Importar Aprovados</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>Foi criada uma lista de discentes com os dados importados. Porém, alguns dados deverão ser importados
		 utilizando-se uma tabela de equivalência como, por exemplo, Matriz Curricular.  
		 Informe qual campo do arquivo de dados indica o valor a ser utilizado na equivalência e,
		 em seguida, informe a equivalência de valores encontrados.</p>
		 <p><b>ATENÇÃO!</b> Alguns valores de equivalência são definidos automaticamente para facilitar o 
		 processo de migração, porém <b>é importante que verifique os valores definidos</b>.</p>
		 <p>Informe as seguinte equivalências:</p>
		 <ul>
		 	<c:forEach items="#{ importaAprovadosTecnicoMBean.atributosComEquivalencia }" var="atributo">
		 		<li style="font-weight: ${importaAprovadosTecnicoMBean.atributoMapeavelEquivalencia.id == atributo.id ? 'bold' : ''};">${atributo.descricao}</li>
	 		</c:forEach>
		 </ul>
	</div>
	<br/>
	<h:form id="form">
		<table class="formulario" width="100%" id="formAtributos">
		<caption>Informe a Equivalência para o Atributo ${ importaAprovadosTecnicoMBean.atributoMapeavelEquivalencia.descricao }</caption>
		<c:if test="${ !importaAprovadosTecnicoMBean.atributoMapeavelEquivalencia.usaSuggestionBox }">
			<c:forEach items="#{ importaAprovadosTecnicoMBean.tabelaEquivalencia }" var="campoEquivalencia">
				<tr>
					<th class="required" width="25%">
						<h:outputText value="#{ campoEquivalencia.key }:" />
					</th>
					<td>
						<h:selectOneMenu value="#{ campoEquivalencia.value.id }" style="max-width:75%;" id="valorEquivalente">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ importaAprovadosTecnicoMBean.valoresEquivalenciaCombo }"/>
						</h:selectOneMenu>
					</td>
					<td></td>
				</tr>
			</c:forEach>
		</c:if>
    	<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="<< Voltar" action="#{importaAprovadosTecnicoMBean.equivalenciaAnterior}"  id="equivalenciaAnterior" rendered="#{!importaAprovadosTecnicoMBean.primeiraEquivalencia}"/>
					<h:commandButton value="Recarregar o Arquivo" action="#{importaAprovadosTecnicoMBean.formUpload}"  id="voltar"/>
					<h:commandButton value="Cancelar" action="#{importaAprovadosTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{importaAprovadosTecnicoMBean.proximaEquivalencia}" id="submeterEquivalencias" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>