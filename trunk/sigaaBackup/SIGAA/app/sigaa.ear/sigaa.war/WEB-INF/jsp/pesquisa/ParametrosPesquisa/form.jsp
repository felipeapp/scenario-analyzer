<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Manuten��o dos Par�metros do M�dulo de Pesquisa
</h2>


<html:form action="/pesquisa/manterParametrosPesquisa" method="post" focus="toleranciaSubmissao">
<table class="formulario" width="95%">
    <caption>Informe os par�metros</caption>
    <tbody>
       <tr>
           <td  width="60%">Toler�ncia para submiss�o de propostas de projetos de pesquisa ap�s encerrado o prazo do edital:</td>
           <td>
           		<html:text property="toleranciaSubmissao" size="5"/> horas
           </td>
		</tr>
       <tr>
           <td>Quantidade m�xima de renova��es poss�veis por projeto de pesquisa:</td>
           <td>
           		<html:text property="qtdMaximaRenovacoes" size="5"/> vezes
           </td>
		</tr>
       <tr>
           <td>Dura��o m�xima permitida para novos projetos:</td>
           <td>
           		<html:text property="duracaoMaximaProjetos" size="5"/> meses
           </td>
		</tr>
		<tr>
           <td>Limite de solicita��es de cotas por projeto:</td>
           <td>
           		<html:text property="limiteCotasProjeto" size="5"/> cotas
           </td>
		</tr>
       <tr>
           <td>Limite de solicita��es de cotas por orientador:</td>
           <td>
           		<html:text property="limiteCotasOrientador" size="5"/> cotas
           </td>
		</tr>
		<tr>
           <td>Dia limite para altera��es de bolsistas efetivarem-se no m�s corrente:</td>
           <td>
           		<html:text property="limiteSubstituicao" size="5"/>
           </td>
		</tr>
		<tr>
           <td>Email para recebimento de notifica��o de altera��es de bolsistas:</td>
           <td>
           		<html:text property="emailNotificacao" size="30"/>
           </td>
		</tr>
		<tr>
           <td>Email para recebimento de notifica��es de inven��o:</td>
           <td>
           		<html:text property="emailNotificacaoInvencao" size="30"/>
           </td>
		</tr>
		<tr>
           <td>Permite envio de relat�rios parciais pelos alunos de inicia��o cient�fica?</td>
           <td>
           		<html:radio property="envioRelatorioParcial" styleClass="noborder" value="true" />Sim
   			<html:radio property="envioRelatorioParcial" styleClass="noborder" value="false" />N�o
           </td>
		</tr>
		<tr>
           <td>Permite envio de resumos do CIC independentes?</td>
           <td>
           		<html:radio property="resumoIndependente" styleClass="noborder" value="true" />Sim
   				<html:radio property="resumoIndependente" styleClass="noborder" value="false" />N�o
           </td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
		<td colspan="2">
	   		<html:button dispatch="persist" value="Confirmar altera��o"/>
	   		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		</td>
		</tr>
	</tfoot>
</table>
</html:form>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>