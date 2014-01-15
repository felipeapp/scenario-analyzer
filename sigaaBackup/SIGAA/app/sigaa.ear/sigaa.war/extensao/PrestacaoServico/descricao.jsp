<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Presta��o de Servi�os</h2>
<br>


<h:form> 
<table class=formulario width="95%">

	<caption class="listagem">Dados da Presta��o de Servi�os</caption>


	<tr>
		<td><i> Descri��o sucinta do servi�o, contendo introdu��o, justificativa, objetivos, metodologia e metas. </i></td>
	</tr>
	<tr>
		<td>		
			<b> Descricao do Servi�o:</b><br>
			<h:inputTextarea id="descricao" value="#{prestacaoServico.obj.descricao}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>


	<tr>
		<td><i> Furmule o problema que o servi�o pretende resolver, resaltando antecedentes hist�ricos. 
		Apresente os dados (informa��es) que forem necess�rias para caracterizar a situa��o atual e as circunst�ncias
		que reclamam ou favorecem a execu��o do servi�o, a localiza��o geogr�fica a ser atendida, o p�blico alvo, bem
		como os resultados esperados.
		 </i></td>
	</tr>
	<tr>
		<td>
			<b> Justificativa:</b><br>
			<h:inputTextarea id="justificativa" value="#{prestacaoServico.obj.justificativa}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>



	<tr>
		<td><i>Determine um objetivo geral que defina de forma clara as diretrizes 
		do projeto e tantos objetivos espec�ficos quanto forem necess�rios para conduzir as a��es do prjeto.
		 </i></td>
	</tr>
	<tr>
		<td>
			<b> Objetivo(s) Geral(is) e Espec�fico(s):</b><br>
			<h:inputTextarea id="objetivos" value="#{prestacaoServico.obj.objetivos}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>


	<tr>
		<td><i>Entende-se por metas os alvos que devem ser atingidos para que os objetivos espec�ficos sejam alcan�ados. Consulte os objetivos
		Espec�ficos acima, defina as metas para alcan�a-los, quantifique-as e, se poss�vel, encontre uma medida correspondente.</i></td>
	</tr>
	<tr>
		<td>
			<b> Metas:</b><br>
			<h:inputTextarea id="metas" value="#{prestacaoServico.obj.metas}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>

	<tr>
		<td><i>Descreva os modos e formas de desnvolvimento do seu projeto, explicando como o servi�o dever� ser executado, quais as t�cnicas e
		procedimentos a serem adotados (estudos, levantamentos, treinamentos, etc)</i></td>
	</tr>
	<tr>
		<td>
			<b> Metodologia:</b><br>
			<h:inputTextarea id="metodologia" value="#{prestacaoServico.obj.metodologia}" rows="3" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>

<tfoot>
<tr> <td colspan=2>
	<h:commandButton value="<< Voltar" action="#{prestacaoServico.irTelaServico}" />
	<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" />
	<h:commandButton value="Avan�ar >>" action="#{prestacaoServico.irTelaResumo}" />
</td> </tr>
</tfoot>

</h:form>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>