<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroSolicitacaoProrrogacaoPrazo.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Solicitação de Prorrogação de Prazo
</h2>

<html:form action="/ensino/latosensu/cadastroSolicitacaoProrrogacaoPrazo?dispatch=persist" method="post" focus="obj.descricao" styleId="form">
    <table class="formulario" width="80%">
		<caption>Dados da Solicitação</caption>
		<html:hidden property="obj.id" />
		<tbody>
			<tr>
				<th>Curso:</th>
				<td colspan="2">
					<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_LATO %>">
						<html:select property="obj.cursoLato.id" styleId="curso">
							<html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
							<html:options collection="cursos" property="id" labelProperty="nome" />
						</html:select>
						<span class="required"></span>
					</ufrn:checkRole>
					<ufrn:checkRole papel="<%= SigaaPapeis.COORDENADOR_LATO %>">
						${ cursoLato.nome }
						<html:hidden property="obj.cursoLato.id" value="${ cursoLato.id }"/>
					</ufrn:checkRole>
				</td>
			</tr>
			<tr>
 				<th>Quantidade de dias:</th>
				<td><html:text property="obj.quantidadeDias" maxlength="3" size="3" /><span class="required">&nbsp;</span></td>
            </tr>
            <tr>
            	<th>Justificativa:</th>
				<td>
					<html:textarea property="obj.justificativa"  cols="60" rows="5"/>
					<span class="required">&nbsp;</span>
				</td>
			</tr>
        </tbody>
		<tfoot>
   			<tr>
				<td colspan="2">
				<html:submit value="Confirmar"/>
                <input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
                </td>
			</tr>
		</tfoot>
	</table>
</html:form>
<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>