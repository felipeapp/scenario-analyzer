<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<script type="text/javascript">
	function mostraContato(bool) {

		if (bool) {
			Element.show('contato');
		} else {
			Element.hide('contato');
		}

	};
	function metodoAvaliacao(tipo) {

		if (tipo == 1) {
			Element.show('nota');
			Element.hide('conceito');
		} else {
			Element.show('conceito');
			Element.hide('nota');
		}

	};
</script>

<input type="hidden" id="linkAjuda" value="/manuais/lato/Proposta/3_processo_seletivo.htm">

<html:form action="/ensino/latosensu/criarCurso" method="post" onsubmit="return validateLatoSelecaoCursoForm(this);" >
<table class="formulario" width="95%">
<caption>Processo Seletivo e Avalia��o do Aluno</caption>
<tr>
<td colspan="2">
	<table class="subFormulario" width="100%">
	<caption>Dados do Processo Seletivo</caption>
	<tr><td colspan="4"><b>a) Inscri��o para sele��o</b></td></tr>
		<tr>
			<th width="30%" class="required">In�cio das Inscri��es:</th>
			<td width="30%">
			<ufrn:calendar property="inicioInscSelecao" />
			</td>
			<th style="text-align: left;">Fim das Inscri��es:
			<html:img page="/img/required.gif" /> 
			<ufrn:calendar property="fimInscSelecao" />
			</th>
		</tr>
		<tr>
			<th class="required">
			Requisitos para Inscri��o: 
			</th>
			<td colspan="3">
			<ufrn:textarea property="proposta.requisitos"  cols="80" rows="5" maxlength="200"/>
			</td>
		</tr>
		<tr>
			<th>Publicar:</th>
			<td colspan="3">
				<html:radio property="proposta.publicar" styleClass="noborder" value="true" onclick="javascript:mostraContato(true);" />Sim
	   			<html:radio property="proposta.publicar" styleClass="noborder" value="false" onclick="javascript:mostraContato(false);" />N�o
				<ufrn:help img="/img/ajuda.gif">Esta op��o informa se a Pr�-Inscri��o Online estar� dispon�vel na �rea P�blica 
					do SIGAA durante o per�odo de inscri��es para sele��o</ufrn:help>
			</td>
		</tr>
		</tr>
		<tr id="contato">
			<th>Informa��es p�blicas para contato:</th>
			<td align="left" colspan="4">
			<ufrn:textarea property="proposta.contatos"  cols="80" rows="7"/>
			</td>
		</tr>
		<tr><td colspan="4"><b>b) Sele��o</b></td></tr>
		<tr>
			<th width="30%" class="required">In�cio do Processo Seletivo:</th>
			<td>
			<ufrn:calendar property="inicioSelecao" />
			</td>
			<th style="text-align: left;">Fim do Processo Seletivo:
				<html:img page="/img/required.gif" /> 
				<ufrn:calendar property="fimSelecao" />
			</th>
		</tr>
		<tr>
			<th>
			Forma de Sele��o:
			</th>
			<td colspan="4">
					<c:forEach items="${formasSelecao}" var="formaSelecao" varStatus="pos">
		    				<br/>
			    			<input type="checkbox" name="formasSelecaoProposta" value="${ formaSelecao.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasSelecaoProposta}" var="latoFormaSelecaoBean">
										<c:if test="${latoFormaSelecaoBean.formaSelecao.id == formaSelecao.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ formaSelecao.descricao }
					</c:forEach>
			</td>
		</tr>

	</table>
</td>
</tr>
<tr>
<td colspan="2">
	<table class="subFormulario" width="100%">
	<caption>Processo de Avalia��o do desempenho do aluno no Curso</caption>
	<tr>
			<th>
			Formas de Avalia��o:
			</th>
			<td colspan="4">
					<c:forEach items="${formasAvaliacao}" var="formaAvaliacao" varStatus="pos">
		    				<br/>
			    			<input type="checkbox" name="formasAvaliacaoProposta" value="${ formaAvaliacao.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasAvaliacaoProposta}" var="latoFormaAvaliacaoBean">
										<c:if test="${latoFormaAvaliacaoBean.formaAvaliacao.id == formaAvaliacao.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ formaAvaliacao.descricao }
					</c:forEach>
			</td>
		</tr>
	<tr>
		<th class="required">Frequ�ncia M�nima Obrigat�ria (%):</th>
		<td>
		<html:text property="proposta.freqObrigatoria" size="4" onkeyup="formatarInteiro(this)" />
		</td>
	</tr>
	<tr>
    	<th class="required">M�todo de Avalia��o:</th>
    	<td>
    		<html:radio property="proposta.metodoAvaliacao" styleClass="noborder" value="1" onclick="javascript:metodoAvaliacao(this.value);" />Nota
   			<html:radio property="proposta.metodoAvaliacao" styleClass="noborder" value="2" onclick="javascript:metodoAvaliacao(this.value);" />Conceito
    	</td>
    </tr>
	<tr id="nota">
		<th class="required">Nota m�nima para aprova��o:</th>
		<td>
		<html:text property="mediaNota" size="2" onkeyup="formatarInteiro(this)" />
		</td>
	</tr>
	<tr id="conceito">
		<th class="required">Conceito m�nimo para aprova��o:</th>
		<td>
		<html:select property="mediaConceito">
			<html:option value="5.0">A</html:option>
			<html:option value="4.0">B</html:option>
			<html:option value="3.0">C</html:option>
			<html:option value="2.0">D</html:option>
			<html:option value="1.0">E</html:option>
		</html:select>
		</td>
	</tr>
	</table>
</td>
</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<html:button dispatch="gravar" value="Gravar"/>
			<html:button view="proposta" value="<< Voltar" />
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
			<html:button dispatch="recursos" value="Avan�ar >>"/>
		</td>
	</tr>
	</tfoot>
</table>
	<script>
		mostraContato(${cursoLatoForm.proposta.publicar});
		metodoAvaliacao(${cursoLatoForm.proposta.metodoAvaliacao});
	</script>
</html:form>
<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> 
<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
</center>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
