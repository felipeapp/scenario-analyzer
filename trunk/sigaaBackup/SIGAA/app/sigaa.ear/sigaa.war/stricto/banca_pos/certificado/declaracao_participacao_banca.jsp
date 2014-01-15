<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
<br/><br/>

<div class="tituloDeclaracao" align="center"><strong> DECLARAÇÃO </strong></div>
<br><br>
<div class="paragrafo">
Declaramos que ${certificadoBancaPos.obj.sexo ? " o Prof. Dr. " : " a Profa. Dra. " }
    ${certificadoBancaPos.obj.participante}, 
    ${certificadoBancaPos.obj.cpfPassaporte}, participou como
	${certificadoBancaPos.obj.tipoParticipacao} da Comissão Examinadora de
	${certificadoBancaPos.obj.tipoBanca} de
	${certificadoBancaPos.obj.nivelBanca} do(a) pós-graduando(a)
	${certificadoBancaPos.obj.discente}, intitulada:</div>
	<br/>
	<div class="titulo">
	${certificadoBancaPos.obj.titulo}</div>
	<br/>
	<div class="paragrafo">no ${certificadoBancaPos.obj.programa}
	${certificadoBancaPos.obj.unidade} da ${ fn:toUpperCase(configSistema['nomeInstituicao']) } , em sessão pública realizada no dia
	${certificadoBancaPos.obj.dataBanca}.</div>
<br/>
<div class="tituloMembros" align="center"> <strong>Membros da Banca </strong></div>
<br>
<c:forEach items="#{certificadoBancaPos.obj.membros}" var="item">
	<div class="membro">${item.descricaoCertificado }</div>
</c:forEach>
<br/><br/><br/>
<hr></hr>
<center>${certificadoBancaPos.assinatura}</center><br><br>
<br><br>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>