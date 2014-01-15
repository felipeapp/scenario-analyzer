<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema />
	<ufrn:subSistema teste="infantil">
	&gt; Nível Infantil
	</ufrn:subSistema>
	<ufrn:subSistema teste="not infantil">
	&gt; Disciplina
	</ufrn:subSistema>
</h2>

	<table class="formulario" width="95%">
	<ufrn:subSistema teste="infantil">
    <caption>Dados do Nível Infantil</caption>
	</ufrn:subSistema>
	<ufrn:subSistema teste="not infantil">
    <caption>Dados da Disciplina</caption>
	</ufrn:subSistema>

    <tbody>
    <tr>
    	<th>Unidade Responsável:</th>
        <td>
        ${disciplina.unidade.nome}
		</td>
	</tr>

	<c:if test="${disciplina.id > 0 }">
	    <tr>
	    	<th>Código:</th>
	        <td>
	        ${disciplina.codigo }
			</td>
		</tr>
	</c:if>
	<tr>
    	<th>Nome:</th>
        <td>${disciplina.nome }
        </td>
	</tr>

	<tr>
    	<th>Carga Horária:</th>
        <td>
			<ufrn:subSistema teste="infantil">
            ${disciplina.chAula }
			</ufrn:subSistema>
			<ufrn:subSistema teste="not infantil">
    		&nbsp;&nbsp;&nbsp;<i>Aula:</i>
            ${disciplina.chAula }
            &nbsp;&nbsp;&nbsp;<i>Laboratório:</i>
            ${disciplina.chLaboratorio }
            &nbsp;&nbsp;&nbsp;<i>Estágio:</i>
            ${disciplina.chEstagio }
            </ufrn:subSistema>
		</td>
	</tr>
	<tr>
		<th>Pré-requisitos:</th>
		<td>${disciplina.preRequisito }</td>
	</tr>
	<tr>
		<th>Có-requisitos:</th>
		<td>${disciplina.coRequisito }</td>
	</tr>
	<tr>
		<th>Equivalência:</th>
		<td>${disciplina.equivalencia }</td>
	</tr>
	<tr>
    	<th valign="top"> Quantidade de Avaliações:</th>
        <td>
            ${disciplina.numUnidades}
        </td>
	</tr>
	<tr>
    	<th valign="top"> Ementa:</th>
        <td>
            ${disciplina.ementa}
        </td>
	</tr>
	<tr>
    	<th valign="top">Objetivos:</th>
        <td>
            ${disciplina.programa.objetivos}
        </td>
	</tr>
	<tr>
    	<th valign="top">Conteúdo:</th>
        <td>
            ${disciplina.programa.conteudo}
        </td>
	</tr>
	<tr>
    	<th valign="top">Competências:</th>
        <td>
            ${disciplina.programa.competenciasHabilidades}
        </td>
	</tr>
	<tr>
    	<th valign="top">Metodologia:</th>
        <td>
            ${disciplina.programa.metodologia}
        </td>
	</tr>
	<tr>
    	<th valign="top">Avaliação:</th>
        <td>
            ${disciplina.programa.procedimentosAvaliacao}
        </td>
	</tr>
	<tr>
    	<th valign="top">Referências:</th>
        <td>
            ${disciplina.programa.referencias}
        </td>
	</tr>
	</tbody>

	<tfoot>
		<tr><td colspan="2">
	    	<input value="Voltar" type="button" onclick="javascript:window.history.back();"/>
    	</td></tr>
	</tfoot>

	</table>

<br><br>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
