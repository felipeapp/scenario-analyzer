<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Prestação de Serviços</h2>
<br>


<h:form> 
<table class=formulario width="95%">

	<caption class="listagem">Dados da Prestação de Serviços</caption>


	<tr>
		<td><i> Descrição sucinta do serviço, contendo introdução, justificativa, objetivos, metodologia e metas. </i></td>
	</tr>
	<tr>
		<td>		
			<b> Descricao do Serviço:</b><br>
			<h:inputTextarea id="descricao" value="#{prestacaoServico.obj.descricao}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>


	<tr>
		<td><i> Furmule o problema que o serviço pretende resolver, resaltando antecedentes históricos. 
		Apresente os dados (informações) que forem necessárias para caracterizar a situação atual e as circunstâncias
		que reclamam ou favorecem a execução do serviço, a localização geográfica a ser atendida, o público alvo, bem
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
		do projeto e tantos objetivos específicos quanto forem necessários para conduzir as ações do prjeto.
		 </i></td>
	</tr>
	<tr>
		<td>
			<b> Objetivo(s) Geral(is) e Específico(s):</b><br>
			<h:inputTextarea id="objetivos" value="#{prestacaoServico.obj.objetivos}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>


	<tr>
		<td><i>Entende-se por metas os alvos que devem ser atingidos para que os objetivos específicos sejam alcançados. Consulte os objetivos
		Específicos acima, defina as metas para alcança-los, quantifique-as e, se possível, encontre uma medida correspondente.</i></td>
	</tr>
	<tr>
		<td>
			<b> Metas:</b><br>
			<h:inputTextarea id="metas" value="#{prestacaoServico.obj.metas}" rows="4" cols="110" readonly="#{prestacaoServico.readOnly}"/>
		</td>
	</tr>

	<tr>
		<td><i>Descreva os modos e formas de desnvolvimento do seu projeto, explicando como o serviço deverá ser executado, quais as técnicas e
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
	<h:commandButton value="Avançar >>" action="#{prestacaoServico.irTelaResumo}" />
</td> </tr>
</tfoot>

</h:form>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>