<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Manutenção dos Parâmetros do Módulo de Pesquisa
</h2>


<html:form action="/pesquisa/manterParametrosPesquisa" method="post" focus="toleranciaSubmissao">
<table class="formulario" width="95%">
    <caption>Informe os parâmetros</caption>
    <tbody>
       <tr>
           <td  width="60%">Tolerância para submissão de propostas de projetos de pesquisa após encerrado o prazo do edital:</td>
           <td>
           		<html:text property="toleranciaSubmissao" size="5"/> horas
           </td>
		</tr>
       <tr>
           <td>Quantidade máxima de renovações possíveis por projeto de pesquisa:</td>
           <td>
           		<html:text property="qtdMaximaRenovacoes" size="5"/> vezes
           </td>
		</tr>
       <tr>
           <td>Duração máxima permitida para novos projetos:</td>
           <td>
           		<html:text property="duracaoMaximaProjetos" size="5"/> meses
           </td>
		</tr>
		<tr>
           <td>Limite de solicitações de cotas por projeto:</td>
           <td>
           		<html:text property="limiteCotasProjeto" size="5"/> cotas
           </td>
		</tr>
       <tr>
           <td>Limite de solicitações de cotas por orientador:</td>
           <td>
           		<html:text property="limiteCotasOrientador" size="5"/> cotas
           </td>
		</tr>
		<tr>
           <td>Dia limite para alterações de bolsistas efetivarem-se no mês corrente:</td>
           <td>
           		<html:text property="limiteSubstituicao" size="5"/>
           </td>
		</tr>
		<tr>
           <td>Email para recebimento de notificação de alterações de bolsistas:</td>
           <td>
           		<html:text property="emailNotificacao" size="30"/>
           </td>
		</tr>
		<tr>
           <td>Email para recebimento de notificações de invenção:</td>
           <td>
           		<html:text property="emailNotificacaoInvencao" size="30"/>
           </td>
		</tr>
		<tr>
           <td>Permite envio de relatórios parciais pelos alunos de iniciação científica?</td>
           <td>
           		<html:radio property="envioRelatorioParcial" styleClass="noborder" value="true" />Sim
   			<html:radio property="envioRelatorioParcial" styleClass="noborder" value="false" />Não
           </td>
		</tr>
		<tr>
           <td>Permite envio de resumos do CIC independentes?</td>
           <td>
           		<html:radio property="resumoIndependente" styleClass="noborder" value="true" />Sim
   				<html:radio property="resumoIndependente" styleClass="noborder" value="false" />Não
           </td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
		<td colspan="2">
	   		<html:button dispatch="persist" value="Confirmar alteração"/>
	   		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		</td>
		</tr>
	</tfoot>
</table>
</html:form>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>